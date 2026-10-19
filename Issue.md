# Personal Expense Manager — Code Review

I've now got the main parts of your Core Java project. Let's review it as a real software project, focusing on bugs that can produce incorrect financial results, then improve the design without throwing away your implementation.

Your project already has a good foundation:

- OOP: `Transaction` is an abstract class, with `Income` and `Expense` subclasses.
- Separation of concerns: `model`, `service`, `menu`, and `util` packages.
- Collections: `HashMap` for transaction records and category budgets.
- Business logic: budget tracking, transaction editing/deletion, and reports.
- Exception handling: a custom `InvalidAmountException`.

`FileStorage.java` and `PersonalExpenseManager.md` are still pending, so we'll focus on the current in-memory application first.

## 1. Fix the highest-priority bugs first

These are the issues I'd address before adding file storage.

### Bug 1 — Expense validation rejects valid amounts below 1

File: `model/Expense.java`

Your current condition is:

```
if (amount > -1)
    throw new InvalidAmountException("Expenses Should in Negative");
```

This means `-0.50` is rejected even though it's a valid negative expense. Your application also accepts `NaN` because comparisons with `NaN` are false.

Hint: Your rule should be "an expense must be finite and strictly negative."

Test these values:

| Amount                     | Expected result |
| -------------------------- | --------------- |
| `-500.0`                   | Accept          |
| `-0.50`                    | Accept          |
| `0.0`                      | Reject          |
| `500.0`                    | Reject          |
| `Double.NaN`               | Reject          |
| `Double.POSITIVE_INFINITY` | Reject          |

Also check `Income.java`: its validation should reject non-finite values too.

### Bug 2 — Editing an expense's category breaks budget totals

File: `service/TransactionService.java`

In `editReordByID()`, your category-edit branch changes the expense's category:

```
categoryValidator();
expenseTracker.get(id).setCategory(category);
```

But it doesn't adjust either category's recorded expenses.

For example:

1. Food budget records ₹500 in expenses.
2. You edit that transaction and change its category from `Food` to `Transport`.
3. The transaction now says `Transport`, but the budget totals still attribute the ₹500 to `Food`.

Hint: Before changing the category, retain the old category and amount. Then update the old category's total and the new category's total before or as part of applying the change.

Test scenario:

- Food expense: `-500`
- Transport expense: `-200`
- Move the first expense to Transport.

Expected totals:

- Food: `0` from these transactions
- Transport: `-700` from these transactions

Your budget update methods currently use negative expense values, so preserve that convention consistently.

### Bug 3 — Reports count the same transactions repeatedly

File: `service/ReportService.java`

You declared:

```
private static int incomeCount;
private static int expenseCount;
```

And increment them inside `getTotalIncomeAmount()` and `getTotalExpenseAmount()`.

Those methods run every time a report is generated. Since the counters are static and never reset, repeated reports produce incorrect counts.

For example, if you have 5 income records, generating the report three times can show 15 incomes.

Hint: A report should calculate its counts from the current transaction collections each time. Consider whether these counters need to be static fields at all.

Test: create 5 income records and 3 expense records, then generate the report three times. The counts should remain 5 incomes and 3 expenses on every report.

### Bug 4 — Reports can show nonexistent highest transactions

File: `service/ReportService.java`

You initialize the highest-transaction IDs to Java's default integer value, `0`, and then call:

```
showHighestIncomeDetails();
showHighestExpenseDetails();
```

This is problematic if the user has income records but no expenses, or vice versa. The report attempts to search for ID `0`, which doesn't exist.

Hint: Handle each transaction type independently. If there are no income records, don't print highest-income details; do the equivalent for expenses.

Also test the completely empty application. It should show a clean "No transactions found" message rather than stale or nonexistent record details.

### Bug 5 — Expense amounts are not normalized at input

File: `service/TransactionService.java`

Your expense model requires a negative amount, but your input prompt simply asks:

```
System.out.print("Amount : ");
```

A user will naturally enter `500` for a ₹500 expense. Your `Expense` constructor then throws an exception.

You have two reasonable design choices:

- Option A: Ask users to enter positive amounts and convert them internally to negative values for expenses.
- Option B: Require negative input and explain that clearly in the prompt.

I'd recommend Option A for usability. Keep the internal convention of positive income and negative expense values, but let users enter ordinary positive amounts.

Be careful when implementing this: your edit flow must follow the same rule as your add flow, or the application will behave inconsistently.

## 2. Budget logic needs a few important corrections

### Bug 6 — Removing or creating a budget can accept invalid categories

File: `service/BudgetService.java`

In `setBudgetCategory()`, if a category doesn't exist, you ask whether to create it. If the user answers `n`, execution can still reach:

```
categoryBudget.put(category, new Budget(budget, expense));
```

That allows a budget to be created for a category the user explicitly declined to add.

Hint: After the user declines, exit the method before creating the budget. Think about the difference between a category existing in `Category` and a category having a budget in `categoryBudget`.

Test: try to set a budget for `Travel`, decline to create the category, and verify that no budget was created.

### Bug 7 — The special default expense value complicates calculations

File: `service/BudgetService.java`

You use:

```
DEFAULT_EXPENSE_FOR_BUDGET = -0.00001;
```

This sentinel represents "no expense recorded yet." But it is also a numeric amount, so it can leak into calculations and displayed results.

For example, a newly created budget could show a tiny expense even though the user hasn't spent anything.

Hint: Consider whether you actually need a special value for "no expenses." A budget with no expenses can naturally start at `0.0`.

You'll need to update the following methods together:

- `Budget.java` — allow a zero expense total.
- `BudgetService.java` — initialize expense totals to zero.
- `updateChangeExpense()` and `updateIncrimentExpense()` — remove special sentinel handling.
- `removeBudgetCategory()` and `adjustDeletedOrExistingExpense()` — simplify the associated calculations.

Don't change just one of these methods; they currently depend on the sentinel convention.

### Bug 8 — Budget expense getters can throw unexpected exceptions

File: `service/BudgetService.java`

These methods assume the category exists:

```
public double getCategoryExpense(String category)
public double getCategoryBGT(String category)
```

If a category isn't present in `categoryBudget`, calling `get()` returns `null`. Calling a method on that result throws a `NullPointerException`.

Hint: Decide which methods require an existing category and which should handle missing categories. Validate the category before dereferencing the returned `Budget`.

### Bug 9 — Budget totals need consistent update rules

Your expense-edit logic currently uses both `updateChangeExpense()` and `updateIncrimentExpense()`. The distinction is important:

- Increment: add a new expense to the existing total.
- Change: replace the existing total with a recalculated total.

Write down the expected total before and after each operation.

| Operation             | Example                | Expected category total |
| --------------------- | ---------------------- | ----------------------- |
| Add expense           | Add `-100`             | `-100`                  |
| Add another           | Add `-50`              | `-150`                  |
| Edit first expense    | Change `-100` to `-80` | `-130`                  |
| Delete second expense | Delete `-50`           | `-80`                   |

These calculations should work regardless of whether the budget was created before the first expense.

Also test deletion when the budget category has already been removed. Your `deletedCategory` map adds extra state to manage, so make sure the transaction total doesn't become incorrect when the category is recreated.

## 3. Input and date validation

### Bug 10 — `DateUtil.isValidDateV2()` is unreliable

File: `util/DateUtil.java`

Your current method checks for a string length of `9`, but the standard ISO date format `yyyy-MM-dd` has 10 characters.

There are other issues too:

- It can throw `NumberFormatException` for malformed numeric fields.
- It doesn't reliably validate the separators.
- The month/day rules don't cover all valid dates.
- Its February handling incorrectly rejects February 1–27 in leap years and February 1–27 in non-leap years.

Fortunately, your `TransactionService` currently uses `isValidDate()`, which relies on `LocalDate.parse()` and is a much better starting point.

Hint: Keep a single reliable date-validation implementation based on `LocalDate.parse()`. Remove the duplicate manual calendar logic unless you have a specific reason to maintain it.

Also consider what should happen when the date is invalid: your current code silently substitutes today's date. For financial records, it's better to tell the user the date is invalid and ask again.

### Bug 11 — Invalid numeric input silently becomes zero

File: `util/InputValidator.java`

For example:

```
catch (Exception e) {
    return 0.0;
}
```

If the user types `abc` instead of an amount, the method returns `0.0`. This loses the distinction between invalid input and a legitimate numeric value.

Hint: Consider validating input in a loop: keep asking until the user enters a valid number. Catch the specific parsing exception instead of every exception.

For money, also check that the value is finite. `Double.parseDouble()` accepts `NaN` and infinity, which should not be valid transaction amounts.

### Bug 12 — The category validator has a null-checking problem

File: `service/TransactionService.java`

Your condition is:

```
if (category.isEmpty() || category.equals(null))
```

The second check cannot safely handle a null `category`, because `category.isEmpty()` is evaluated first. A null value would cause a `NullPointerException`.

In your current Scanner-driven flow, the user will usually provide a non-null string, so this may not be the first bug you encounter.

Hint: Think about the order of null and empty checks. Also consider whitespace-only input, such as `"   "`, which isn't technically empty but probably shouldn't be a category name.

## 4. Transaction editing and reports

### Bug 13 — Editing an expense amount can fail when its budget doesn't exist

File: `service/TransactionService.java`

In the amount-edit branch, you call:

```
double newCategoryExpense =
    bgtService.getCategoryExpense(recordCategory) + adjustableAmount;
```

If that category's budget was removed, `getCategoryExpense()` can fail because the category is no longer in `categoryBudget`.

Hint: Think about whether transaction records should remain editable when a budget is removed. A transaction and a budget are different pieces of data; removing one shouldn't necessarily make the other unusable.

### Bug 14 — Editing a category doesn't update the budget

This is related to Bug 2, but the same issue applies to the interaction between the transaction and the budget services. Your `Expense.setCategory()` changes the transaction only.

Hint: Treat a category change as two accounting adjustments: reverse the amount from the old category and apply it to the new category.

### Bug 15 — `HashMap` iteration order isn't guaranteed

Files: `TransactionService.java`, `BudgetService.java`

You display records using `HashMap.values()` and `keySet()`. This doesn't guarantee chronological or insertion order.

Your program might show the records in a different order than expected.

Hint: For a better user experience, sort transactions by date or ID when displaying them. You can use a `Comparator` and a list of records without replacing your existing maps immediately.

## 5. OOP and design improvements

These aren't all immediate bugs, but they will improve the quality of your project.

`Category.java` — encapsulation

`getAllCategorys()` returns the actual mutable `HashSet`. Any caller can add or remove categories without going through `addCategory()`.

Hint: Keep the internal collection private and return an unmodifiable view or defensive copy. Consider making the class `final` with a private constructor because it acts as a utility class.

Money representation

You're using `double` for all amounts. Floating-point arithmetic can introduce rounding errors.

Hint: For a finance application, consider learning `BigDecimal` later. It supports decimal arithmetic more appropriate for money. You don't need to migrate everything immediately, but format amounts consistently to two decimal places.

`Transaction.java` — validation during construction

Your superclass constructor calls `setAmount()`, which calls the overridden `isValidAmount()` method before the subclass constructor finishes.

Hint: Avoid calling overridable methods from constructors where possible. You can explore validating constructor arguments through a safer design as you refine the model.

`Main.java` — exception handling

Your general exception handler prints a stack trace, consumes another input line, and exits the application.

Hint: During development, stack traces are useful. For expected user-input errors, display a friendly message and let the application continue. Avoid catching every exception at the same level without considering recovery.

## 6. Fix these issues in this order

Don't try to fix everything at once. Use this checklist as your development plan.

## Bug-fixing checklist

0/10 complete

Fix amount validation

Expense, Income, NaN and infinity handling

Fix expense input

Accept positive user input and normalize it internally

Fix category changes

Update old and new category expense totals

Fix report counts

Recalculate counts every time a report runs

Fix empty highest-transaction results

Handle income-only and expense-only histories

Remove the budget sentinel

Use zero as the initial expense total

Fix category creation cancellation

Do not create a budget after the user declines

Fix date and numeric validation

Reject invalid input and request it again

Test deletion and editing

Include transactions whose budget was removed

Sort displayed records

Use a Comparator for predictable output

## 7. Test your project before adding file storage

Here are five scenarios worth testing manually:

| Scenario                                       | Expected result                    |
| ---------------------------------------------- | ---------------------------------- |
| Add an expense of ₹0.50                        | Accepted and stored consistently   |
| Generate a report three times                  | Counts remain unchanged            |
| Move a ₹500 expense from Food to Transport     | Both category totals update        |
| Delete all income records but keep expenses    | Highest-income details are omitted |
| Decline creation of an unknown budget category | No budget is created               |

Once these pass, test invalid dates, blank categories, repeated deletions, and edits to transactions whose budgets have been removed.

My recommendation: Start with Bugs 1–5. They affect the correctness of your transactions and financial reports. Then fix the budget sentinel and category validation before working on persistent storage.

You don't need to rewrite the project. Your current architecture is a reasonable starting point; the goal is to make the logic reliable one issue at a time.