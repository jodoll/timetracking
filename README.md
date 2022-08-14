# Time Tracking

This is a simple utility to track work items via a simple file based structure.
Data is stored in a `.csv` file and is human-readable.
Via different scripts those files can be read and analyzed.

## Description

To use this tool you'll need to create a file that contains your work items.
Here's sneak preview of what such a file could look like,
for an in depth explanation check the [examples folder](examples)
and the [Format Description](#format-description) section.

```csv
# Monday
Mon, 1015, 1030,, TRCK-1032
Mon, 1100, 1145,, Demo
Mon, 1330, 1400,, Time Tracking
Mon, 1400, 1500,, Architecture Meeting
Mon, 1500, 1630, TRCK-1203, As a user I want to log into the app
```

This file will then be parsed by this tool, and you can print a report of it.
Currently, a daily and a task based report are implemented.
Here's an extract of the reports, check the [report](#Report) section
and the [exmaples folder](examples) for more details:

**Task Report**

```
TRCK-1203
 1,5  - Monday
 6,75 - Tuesday
 2,0  - Thursday
 3,25 - Friday
```

**Daily Report**

```
Wednesday
 5,5  - TRCK-1204
 0,5  - Daily
 0,5  - TRCK-1205
 0,5  - Refinement
 0,5  - TRCK-108982
 0,5  - Setting up test device
----------
 8,0  - Total
```

## Usage

As there are currently no releases, you'll currently need to build the tool yourself.
Furthermore, there are no command line options implemented right now, so choose the
[`ReportBuilder`](src/main/kotlin/com/johannesdoll/timetracking/report/ReportBuilder.kt)
you want to use by editing [`Application.kt`](src/main/kotlin/com/johannesdoll/timetracking/Application.kt).  
After that run the build:

```bash
./gradlew jar
```

Then execute the tool with Java and pass the path to your time sheet as single argument:

```bash
java -jar build/libs/timetracking-1.0-SNAPSHOT.jar examples/timesheet.csv
```

## Format Description

Each file stores a single calendar week.
The week starts at Monday, as that's the only valid first day of a week[^start-of-week] (at least ISO says so).
Each line can either be a comment or needs to adhere to the following format:

```
<Day of Week>, <Start Time>, <End Time>, <Ticket Key>, <Description>, <Comment>
```

The First 3 items are required, anything else can be omitted or left empty.
If you leave out a field but want to input a value for a field behind it, you must add the commas.
E.g:
```
Mo, 1030, 1200,,"Foo"
```
In this example the ticket key is left empty and comment can be omitted all together as no fields following it are specified (in fact, there are none).

And, As you probably guessed, `,` is the chosen separator

### Day of Week

One of `Mon`, `Tue`, `Wed`, `Thu`, `Fri`, `Sat`, `Sun`.

### Start Time/End Time
Time in 24 hours format, omitting the colon (`HHmm`).
You may omit a leading `0`.  
So `13:30` would be `1330` and `0930` could also be `930`.

### Ticket Key

Any key to a ticket in some ticket system.
This allows for automatic retrieval of further data for the entry
(but you'll need to specify how to resolve that at some place).

### Description

Description of the Task (like a summary).
Could be resolved automatically via the `<Ticket Key>` or provided manually.

### Comment

Any other remarks you want to make that might be helpful.
This might not even show up in the report and is not used for anything.

### Comments

Lines starting with a `#` are comments and are ignored.
Use them to structure your documents.

## Report

The tool supports different reports (although there is currently no option to switch between them from command line).

Generally speaking the entries in the timesheet are grouped by their [ticket key](#ticket-key),
or if there is none by their [description](#description).
Grouping is case and whitespace sensitive (leading/trailing whitespace is ignored).

Furthermore, grouping is done by day (might be configurable in the future)
and some reports calculate a total over each item (will be also configurable at some point).

Time is calculated in hours, this means base 10, what comes after the dot are not minutes
(you've guessed it: configurable in the future).

### Daily Report

The daily report groups entries by day first and then by task.

The key (or description) is shown, along with the description (if key is given) and comment
of the first item of the ones grouped together respectively.

Totals are calculated for each day.

```
Wednesday
 5,5  - TRCK-1204
 0,5  - Daily
 0,5  - TRCK-1205
 0,5  - Refinement
 0,5  - TRCK-108982
 0,5  - Setting up test device
----------
 8,0  - Total
```

Also have a look at the [example](examples/daily.report) file.

### Task Report

The task report groups entries by task first and then by day.

The key (or description) is shown, along with the description (if key is given) and comment
of the first item of the ones grouped together respectively.

```
TRCK-1203
 1,5  - Monday
 6,75 - Tuesday
 2,0  - Thursday
 3,25 - Friday
```

Also have a look at the [example](examples/task.report) file.

## Footnotes

[^start-of-week]: Not that it would matter in this project. What might happen is that Sunday appears at the end of the
report, that's all.