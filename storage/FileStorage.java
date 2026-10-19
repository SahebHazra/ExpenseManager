package storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import model.Expense;
import model.Income;
import model.Budget;
import util.InvalidAmountException;
import storage.DBConfig;

public class FileStorage {

	    private static final String DB_HOST = DBConfig.getHost();
	    private static final int DB_PORT = DBConfig.getPort();
	    private static final String DB_NAME = DBConfig.getName();
	    private static final String DB_USER = DBConfig.getUser();
	    private static final String DB_PASSWORD = DBConfig.getPassword();

	    private static final String SERVER_URL = String.format("jdbc:mysql://%s:%d/?serverTimezone=UTC", DB_HOST,
		    DB_PORT);
	    private static final String DB_URL = String.format("jdbc:mysql://%s:%d/%s?serverTimezone=UTC", DB_HOST, DB_PORT,
		    DB_NAME);

	private static void ensureDatabaseAndTables() throws Exception {
		// initialize connection pool
		ConnectionPool.init(DB_URL, DB_USER, DB_PASSWORD, 5);
		Connection conn = null;
		Statement st = null;
		try {
			conn = ConnectionPool.getConnection();
			st = conn.createStatement();
			st.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);
		} finally {
			try { if (st != null) st.close(); } catch (Exception e) {}
			ConnectionPool.releaseConnection(conn);
		}
		try {
			conn = ConnectionPool.getConnection();
			st = conn.createStatement();
			String create = "CREATE TABLE IF NOT EXISTS transactions ("
						+ "id INT PRIMARY KEY, "
						+ "date DATE NOT NULL, "
						+ "type VARCHAR(10) NOT NULL, "
						+ "category_or_source VARCHAR(255), "
						+ "amount DOUBLE NOT NULL, "
						+ "note TEXT" + ")";
			st.executeUpdate(create);
			String createCategories = "CREATE TABLE IF NOT EXISTS categories (name VARCHAR(255) PRIMARY KEY)";
			st.executeUpdate(createCategories);
			String createBudgets = "CREATE TABLE IF NOT EXISTS budgets (category VARCHAR(255) PRIMARY KEY, budget DOUBLE NOT NULL, expense DOUBLE NOT NULL)";
			st.executeUpdate(createBudgets);
		} finally {
			try { if (st != null) st.close(); } catch (Exception e) {}
			ConnectionPool.releaseConnection(conn);
		}
	}

	public static Set<String> loadCategories() {
		Set<String> out = new HashSet<>();
		try {
			ensureDatabaseAndTables();
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			try {
				conn = ConnectionPool.getConnection();
				st = conn.createStatement();
				rs = st.executeQuery("SELECT name FROM categories");
				while (rs.next()) {
					out.add(rs.getString(1));
				}
			} finally {
				try { if (rs != null) rs.close(); } catch (Exception e) {}
				try { if (st != null) st.close(); } catch (Exception e) {}
				ConnectionPool.releaseConnection(conn);
			}
		} catch (Exception e) {
			System.out.println("Error loading categories: " + e.getMessage());
		}
		return out;
	}

	public static Map<String, Budget> loadBudgets() {
		Map<String, Budget> out = new HashMap<>();
		try {
			ensureDatabaseAndTables();
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			try {
				conn = ConnectionPool.getConnection();
				st = conn.createStatement();
				rs = st.executeQuery("SELECT category, budget, expense FROM budgets");
				while (rs.next()) {
					String category = rs.getString(1);
					double budget = rs.getDouble(2);
					double expense = rs.getDouble(3);
					try {
						out.put(category, new Budget(budget, expense));
					} catch (InvalidAmountException e) {
						System.out.println("Skipping budget " + category + " due to invalid data");
					}
				}
			} finally {
				try { if (rs != null) rs.close(); } catch (Exception e) {}
				try { if (st != null) st.close(); } catch (Exception e) {}
				ConnectionPool.releaseConnection(conn);
			}
		} catch (Exception e) {
			System.out.println("Error loading budgets: " + e.getMessage());
		}
		return out;
	}

	public static void saveCategory(String category) {
		try {
			ensureDatabaseAndTables();
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = ConnectionPool.getConnection();
				ps = conn.prepareStatement("INSERT IGNORE INTO categories (name) VALUES (?)");
				ps.setString(1, category);
				ps.executeUpdate();
			} finally {
				try { if (ps != null) ps.close(); } catch (Exception e) {}
				ConnectionPool.releaseConnection(conn);
			}
		} catch (Exception e) {
			System.out.println("Error saving category: " + e.getMessage());
		}
	}

	public static void deleteCategory(String category) {
		try {
			ensureDatabaseAndTables();
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = ConnectionPool.getConnection();
				ps = conn.prepareStatement("DELETE FROM categories WHERE name = ?");
				ps.setString(1, category);
				ps.executeUpdate();
			} finally {
				try { if (ps != null) ps.close(); } catch (Exception e) {}
				ConnectionPool.releaseConnection(conn);
			}
		} catch (Exception e) {
			System.out.println("Error deleting category: " + e.getMessage());
		}
	}

	public static void saveOrUpdateBudget(String category, double budget, double expense) {
		try {
			ensureDatabaseAndTables();
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = ConnectionPool.getConnection();
				ps = conn.prepareStatement("REPLACE INTO budgets (category, budget, expense) VALUES (?,?,?)");
				ps.setString(1, category);
				ps.setDouble(2, budget);
				ps.setDouble(3, expense);
				ps.executeUpdate();
			} finally {
				try { if (ps != null) ps.close(); } catch (Exception e) {}
				ConnectionPool.releaseConnection(conn);
			}
		} catch (Exception e) {
			System.out.println("Error saving budget: " + e.getMessage());
		}
	}

	public static void deleteBudget(String category) {
		try {
			ensureDatabaseAndTables();
			Connection conn = null;
			PreparedStatement ps = null;
			try {
				conn = ConnectionPool.getConnection();
				ps = conn.prepareStatement("DELETE FROM budgets WHERE category = ?");
				ps.setString(1, category);
				ps.executeUpdate();
			} finally {
				try { if (ps != null) ps.close(); } catch (Exception e) {}
				ConnectionPool.releaseConnection(conn);
			}
		} catch (Exception e) {
			System.out.println("Error deleting budget: " + e.getMessage());
		}
	}

	public static void loadTransactions(HashMap<Integer, Income> incomeMap, HashMap<Integer, Expense> expenseMap) {
		try {
			ensureDatabaseAndTables();
			Connection conn = null;
			Statement st = null;
			ResultSet rs = null;
			try {
				conn = ConnectionPool.getConnection();
				st = conn.createStatement();
				rs = st.executeQuery("SELECT id, date, type, category_or_source, amount, note FROM transactions");
				while (rs.next()) {
					int id = rs.getInt("id");
					String date = rs.getDate("date").toString();
					String type = rs.getString("type");
					String catOrSource = rs.getString("category_or_source");
					double amount = rs.getDouble("amount");
					String note = rs.getString("note");
					try {
						if ("INCOME".equalsIgnoreCase(type)) {
							incomeMap.put(id, new Income(id, amount, catOrSource, date, note));
						} else if ("EXPENSE".equalsIgnoreCase(type)) {
							expenseMap.put(id, new Expense(id, amount, catOrSource, date, note));
						}
					} catch (InvalidAmountException iae) {
						System.out.println("Skipping record " + id + " due to invalid amount: " + iae.getMessage());
					}
				}
			} finally {
				try { if (rs != null) rs.close(); } catch (Exception e) {}
				try { if (st != null) st.close(); } catch (Exception e) {}
				ConnectionPool.releaseConnection(conn);
			}
		} catch (Exception e) {
			System.out.println("Error loading transactions from MySQL: " + e.getMessage());
		}
	}

	public static void saveTransactions(HashMap<Integer, Income> incomeMap, HashMap<Integer, Expense> expenseMap) {
		try {
			ensureDatabaseAndTables();
			Connection conn = null;
			Statement st = null;
			PreparedStatement ps = null;
			try {
				conn = ConnectionPool.getConnection();
				conn.setAutoCommit(false);
				st = conn.createStatement();
				st.executeUpdate("TRUNCATE TABLE transactions");
				String insertSql = "INSERT INTO transactions (id, date, type, category_or_source, amount, note) VALUES (?,?,?,?,?,?)";
				ps = conn.prepareStatement(insertSql);
				for (Income inc : incomeMap.values()) {
					ps.setInt(1, inc.getID());
					ps.setDate(2, java.sql.Date.valueOf(inc.getDate()));
					ps.setString(3, "INCOME");
					ps.setString(4, inc.getSource());
					ps.setDouble(5, inc.getAmount());
					ps.setString(6, inc.getNote());
					ps.addBatch();
				}
				for (Expense exp : expenseMap.values()) {
					ps.setInt(1, exp.getID());
					ps.setDate(2, java.sql.Date.valueOf(exp.getDate()));
					ps.setString(3, "EXPENSE");
					ps.setString(4, exp.getCategory());
					ps.setDouble(5, exp.getAmount());
					ps.setString(6, exp.getNote());
					ps.addBatch();
				}
				ps.executeBatch();
				conn.commit();
			} finally {
				try { if (ps != null) ps.close(); } catch (Exception e) {}
				try { if (st != null) st.close(); } catch (Exception e) {}
				ConnectionPool.releaseConnection(conn);
			}
		} catch (Exception e) {
			System.out.println("Error saving transactions to MySQL: " + e.getMessage());
		}
	}

}
