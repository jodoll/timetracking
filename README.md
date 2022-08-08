# Time Tracking
This is a simple utility to track work items via a simple file based structure.
Data is stored in a `.csv` file and is human readable.
Via different scripts those files can be read and analyzed.

# Format Description
Each file stores a single calendar week. 
The week starts at Monday, as that's the only valid first day of a week (at least ISO says so).
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
Any key to a ticket in some ticket system. This allows for automatic retrieval of further data for the entry (but you'll need to specify how to resolve that at some place).

### Description
Description of the Task (like a summary). Could be resolved automatically via the `<Ticket Key>` or provided manually.

### Comment
Any other remarks you want to make that might be helpful

### Comments
Lines starting with a `#` are comments and are ignored. Use them to structure your documents.