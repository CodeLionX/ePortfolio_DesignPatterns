# Step-By-Step guide for implementing the *Active Object Pattern*

## Prerequisites

- Please clone the following repository: [ActiveObjectDemo/withoutPattern](https://github.com/CodeLionX/ActiveObjectDemo/tree/withoutPattern)
- Go through the code and understand what it is doing.
- You should have understood how the *Active Object Pattern* works.


## 1. Create the Scheduler class: `QueryScheduler`
We want to start by creating the Scheduler class, which will take care of executing the request concurrently. Therefore we create a new class `QueryScheduler`. Inside this class I have used an `Executor` instance, which can handle tasks automatically.
``` java
public class QueryScheduler {
  private final Executor executor;
  public QueryScheduler(int maxParallelWorkers) {
    this.executor = Executors.newFixedThreadPool(maxParallelWorkers);
  }
}
```
The class `QueryScheduler` needs a method for adding tasks to it and running them one after the other with the `Executor`. The `Executor` will take care of adding the tasks to a queue and we therefore do not need a separate ActivationQueue. As we have specified the number of parallel worker threads it will also ensure that only the given maximum of calls will be executed in parallel.
``` java
public void schedule(Runnable task) {
  executor.execute(task);
}
```

## 2. Create a Request object: `QueryRequest`
The Request object is the objectified version of the method call. It will have the method arguments as attributes and will also get a reference to the Servant to execute the request. I have used `FutureTask` as a super class for getting all required functionality for running, cancelling and retrieving the result of the computation.

Create a new class called `QueryRequest` with the following constructor:
``` java
public class QueryRequest extends FutureTask<String[]> {

    private final String paramFirstName;
    private final String paramLastName;

    public QueryRequest(final DatabaseServant db, final String paramFirstName, final String paramLastName) {
        super(new Callable<String[]>() {
            @Override
            public String[] call() throws Exception {
                return db.queryData(paramFirstName, paramLastName);
            }

        });
        this.paramFirstName = paramFirstName;
        this.paramLastName = paramLastName;
    }
}
```
The constructor of `FutureTask` gets an `Callable`-object as argument. I have created an anonymous inner class that implements the `call()`-method. In this method I used the reference to the Servant and the method arguments to call the `queryData()`-method on the Servant. This method will later be called by the Scheduler, when the task is executed. The only important method in this class is the `run()`-method. It just forwards the call to the super class:
``` java
@Override
public void run() {
  super.run();
}
```

## 4. Implement the Future object: `QueryRequestFuture`
The `QueryRequestFuture` will be used by the client to retrieve the result and check wether the computation was done or not. I have created a new class implementing the `Future`-interface which defines the most common methods used for Futures. In the constructor I have passed a reference to our objectified request (the `QueryRequest` class). Now you just have to implement all the methods of the `Future`-inteface by delegating the calls to the `QueryRequest`. In the `get()`-method you may want to check if the `QueryRequest` is already done or not before you return the result.
``` java
public class QueryRequestFuture implements Future<String[]> {
  private final QueryRequest queryRequest;

  public QueryRequestFuture(QueryRequest queryRequest) {
  	super();
    this.queryRequest = queryRequest;
  }

  @Override
  public String[] get() throws InterruptedException, ExecutionException {
    try {
      if (queryRequest.isDone() && !queryRequest.isCancelled())
        return queryRequest.get();
    } catch (Exception ex) {
      System.err.println("Failed to get result!");
    }
    return null;
  }
  [...]
}
```

## 4. Create a Proxy object for the database: `DatabaseProxy`
Now we will implement a Proxy for our `DatabaseServant` object. It will accept the method call from the client, objectifies it and forwards it to the `RequestScheduler` for execution. The Proxy will have nearly the same interface than the real Servant despite the returning types of the methods. The Proxy will return a Future object. In the constructor we will pass the Servant object and the Scheduler to be flexible. This may need further improvement to hide this information from the client.

The class will only have one method `queryData()`, which will act as a proxy for the `queryData()`-method of the `DatabaseServant`. It objectifies the method call to a `QueryRequest` object and schedules it for concurrent execution with the `QueryScheduler`. The method will return a `QueryRequestFuture` object which allows the client to poll for the result from time to time. See my implementation below:
``` java
public class DatabaseProxy {
  private final DatabaseServant engine;
  private final QueryScheduler scheduler;

  public DatabaseProxy(DatabaseServant engine, QueryScheduler scheduler) {
    this.engine = engine;
    this.scheduler = scheduler;
  }

  public QueryRequestFuture queryData(String firstName, String lastName) {
    QueryRequest objectifiedRequest = new QueryRequest(engine, firstName, lastName);
    QueryRequestFuture future = new QueryRequestFuture(objectifiedRequest);
    scheduler.schedule(objectifiedRequest);
    return future;
  }
}
```

## 5. Test the pattern with a JUnit test class
For testing our implementation we now need a new test case. Therefore copy the `TestWithoutAO` class and call it `ActiveObjectTest` or similar. Now add a new private static field for the number of worker thread you want to use in the `RequestScheduler`:
``` java
private static final int NUMBER_OF_WORKERS = 2;
```
In the `testQueryData()`-method we need to make following changes:
- Add an `QueryScheduler` instance with `NUMBER_OF_WORKERS` worker threads.
``` java
QueryScheduler scheduler = new QueryScheduler(NUMBER_OF_WORKERS);
```
- Create the `DatabaseProxy` object.
``` java
DatabaseProxy databaseProxy = new DatabaseProxy(database, scheduler);
```
- Change the query methods to use the Proxy object instead of the Servant directly and adapt the result data types to match the `QueryRequestFuture` return types. Do this for all three queries.
``` java
QueryRequestFuture future1 = databaseProxy.queryData("Jack", "Miller");
```
- Now we have triggered all request. At this point we could now do other work which will not require the results of the queries. As we do not have to do something else we just wait for the computations to be done and the Futures to be completed. Therfore we use polling in an two seconds intervall. After that we can retrieve the result and use the existing `assertYYY()` methods for testing them.
``` java
while (!future1.isDone() || !future2.isDone() || !future3.isDone()) {
  Thread.sleep(2000);
}
String[] result1 = future1.get();
String[] result2 = future2.get();
String[] result3 = future3.get();
```

As you can see there is not much we have to change in our Client (=> test class). The only few changes relate to the use of the Proxy object despite the real Servant and using polling on the Future to wait for the result.

You can find the whole solution in the master branch of my Github repository [ActiveObjectDemo](https://github.com/CodeLionX/ActiveObjectDemo).
