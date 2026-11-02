package storage;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.Base64;

/** Simple file-backed CRUD storage. Fields are encoded so spaces in notes/categories are preserved. */
public class FileStorage {
    private static final String HEADER = "# ExpenseManager records v2";
    private final File dir = new File("data");
    private final File incomes = new File(dir, "IncomeRecords.txt");
    private final File expenses = new File(dir, "ExpenseRecords.txt");
    private final File idFile = new File(dir, "IdFile.txt");

    public FileStorage() {
        try {
            if (!dir.exists() && !dir.mkdirs()) throw new IOException("Cannot create data directory");
            if (!incomes.exists()) incomes.createNewFile();
            if (!expenses.exists()) expenses.createNewFile();
            if (!idFile.exists()) try (Writer w = new FileWriter(idFile)) { w.write("100"); }
        } catch (IOException e) { throw new IllegalStateException("Cannot initialize storage", e); }
    }

    public synchronized int IncrimentAndGetID() {
        int max = 100;
        for (String[] r : loadIncomeRecords()) max = Math.max(max, id(r));
        for (String[] r : loadExpenseRecords()) max = Math.max(max, id(r));
        try (Scanner s = new Scanner(idFile)) { if (s.hasNextInt()) max = Math.max(max, s.nextInt()); }
        catch (IOException e) { System.out.println("ID read failed: " + e.getMessage()); }
        int next = max + 1;
        try (Writer w = new FileWriter(idFile, false)) { w.write(Integer.toString(next)); return next; }
        catch (IOException e) { System.out.println("ID save failed: " + e.getMessage()); return -1; }
    }

    private int id(String[] r) { try { return Integer.parseInt(r[0]); } catch (Exception e) { return 100; } }
    private String enc(String s) { return Base64.getEncoder().encodeToString((s == null ? "" : s).getBytes(StandardCharsets.UTF_8)); }
    private String dec(String s) { return new String(Base64.getDecoder().decode(s), StandardCharsets.UTF_8); }

    private String[] parse(String line) {
        if (line.isBlank() || line.equals(HEADER)) return null;
        if (line.startsWith("v2|")) {
            String[] parts = line.split("\\|", -1);
            if (parts.length != 6) return null;
            try { String[] r = new String[5]; for (int i=0;i<5;i++) r[i]=dec(parts[i+1]); return r; }
            catch (IllegalArgumentException e) { return null; }
        }
        // Best-effort import of legacy whitespace-separated rows.
        String[] old = line.trim().split("\\s+", 5);
        if (old.length < 4) return null;
        return new String[] {old[0], old[1], old[2], old[3], old.length == 5 ? old[4] : "N/A"};
    }

    private List<String[]> read(File f) {
        List<String[]> rows = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line; while ((line=br.readLine()) != null) { String[] r=parse(line); if (r!=null) rows.add(r); }
        } catch (IOException e) { System.out.println("Read failed ("+f.getName()+"): "+e.getMessage()); }
        return rows;
    }
    public List<String[]> loadIncomeRecords() { return read(incomes); }
    public List<String[]> loadExpenseRecords() { return read(expenses); }

    private String line(String[] r) {
        return "v2|"+enc(r[0])+"|"+enc(r[1])+"|"+enc(r[2])+"|"+enc(r[3])+"|"+enc(r[4]);
    }
    private boolean write(File f, List<String[]> rows) {
        File tmp = new File(f.getPath()+".tmp");
        try (BufferedWriter w = new BufferedWriter(new FileWriter(tmp))) {
            w.write(HEADER); w.newLine();
            for (String[] r:rows) { w.write(line(r)); w.newLine(); }
        } catch (Exception e) { tmp.delete(); System.out.println("Write failed: "+e.getMessage()); return false; }
        if (f.exists() && !f.delete()) { tmp.delete(); return false; }
        if (!tmp.renameTo(f)) { tmp.delete(); return false; }
        return true;
    }
    private boolean add(File f, int id, double amount, String date, String value, String note) {
        List<String[]> rows=read(f);
        for (String[] r:rows) if (id(r)==id) return false;
        rows.add(new String[]{Integer.toString(id),Double.toString(amount),date,value,note});
        return write(f,rows);
    }
    public synchronized boolean addIncomeRecord(int id,double amount,String date,String source,String note) { return add(incomes,id,amount,date,source,note); }
    public synchronized boolean addExpenseRecord(int id,double amount,String date,String category,String note) { return add(expenses,id,amount,date,category,note); }

    private boolean update(File f,int id,double amount,String date,String value,String note) {
        List<String[]> rows=read(f);
        for (String[] r:rows) if (id(r)==id) {
            r[1]=Double.toString(amount); r[2]=date; r[3]=value; r[4]=note; return write(f,rows);
        }
        return false;
    }
    public synchronized boolean updateIncomeRecord(int id,double amount,String date,String source,String note) { return update(incomes,id,amount,date,source,note); }
    public synchronized boolean updateExpenseRecord(int id,double amount,String date,String category,String note) { return update(expenses,id,amount,date,category,note); }
    private boolean remove(File f,int id) {
        List<String[]> rows=read(f); boolean removed=rows.removeIf(r->id(r)==id);
        return removed && write(f,rows);
    }
    public synchronized boolean removeIncomeRecord(int id) { return remove(incomes,id); }
    public synchronized boolean removeExpenseRecord(int id) { return remove(expenses,id); }
    public int getIncomeCount() { return loadIncomeRecords().size(); }
    public int getExpenseCount() { return loadExpenseRecords().size(); }
    public void getAllIncomeRecords() { print(loadIncomeRecords(),"SOURCE"); }
    public void getAllExpenseRecords() { print(loadExpenseRecords(),"CATEGORY"); }
    private void print(List<String[]> rows,String label) {
        if(rows.isEmpty()){System.out.println("Records not found");return;}
        System.out.printf("%-8s %-12s %-12s %-18s %s%n","ID","AMOUNT","DATE",label,"NOTE");
        for(String[] r:rows) System.out.printf("%-8s %-12s %-12s %-18s %s%n",r[0],r[1],r[2],r[3],r[4]);
    }
    public boolean deleteAllRecordsOfIncome() { return write(incomes,new ArrayList<>()); }
    public boolean deleteAllRecordsOfExpense() { return write(expenses,new ArrayList<>()); }
}