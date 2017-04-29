# 3. Concurrency

## 3.1 Concurrency challenge

Concurrency means that any object that represents a shared resource in a distributed system must be responsible for ensuring that it operates correctly in a concurrent environment. **(Cite the book!)**

Subject to the protocol, the concurrency challenge mainly occurs when several commands are processed simultaneously on a server. This would cause two types of problem: *thread interference* and *memory consistency errors* ([https://docs.oracle.com/javase/tutorial/essential/concurrency/sync.html](https://docs.oracle.com/javase/tutorial/essential/concurrency/sync.html "Synchronization (The Java™ Tutorials > Essential Classes > Concurrency)")). The data of resource and resourceList might not be concurrent.

**An Example**

A simplified process of PUBLISH is:

    if (resource exist in resourceList) {
        resourceCollection.update(resource);
    } else {
        resourceCollection.add(resource);
    
However, if two thread (A and B) invokes thread roughly at the same time, it is possible to have such process sequence:

    Thread A: look if resource exist in resourceList, found no such resource
    Thread B: look if resource exist in resourceList, found no such resource
    Thread A: Add the resource with no owner (public)
    Thread B: Add the resource with owner "Unimelb" (other info is same as A)

B should bot be able to add/update the resource. Depends on the data structure, this either creates a new resource with the same primary key or overwrites the resource.

We use `synchronized` to ensure concurrency. For this system, we should never allow multiple threads 'write' operations to the same resource simultaneously. See the process of command as a transaction is an option. But for this project, trivial methods using `synchronized` is enough. To avoid *starvation*, we `synchronized` at a relatively low level.

Moreover, if the system supports more advanced features, such as *file replication* and *partitioning*, the concurrency challenge is much more complex.

## 3.2 Revisions to protocol