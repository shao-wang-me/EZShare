# 3. Concurrency

## 3.1 Concurrency Challenge

Concurrency means that "any object that represents a shared resource in a distributed system must be responsible for ensuring that it operates correctly in a concurrent environment" (Coulouris, Dollimore, and Kindberg). 

Subject to the protocol, the concurrency challenge mainly occurs when several commands are processed simultaneously on a server, the data of resource and resourceList need to be concurrent.

**An Example: *Race Condition***

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

We use `synchronized` to ensure concurrency. For this system, we should never allow multiple threads 'write' operations to the same resource simultaneously. See the process of command as a transaction is an option. However, for this project, trivial methods using `synchronized` is enough. To avoid *starvation*, we `synchronized` at a relatively low level. Also, we use `volatile` keyword to avoid memory consistency errors.

Moreover, if the system supports more advanced features, such as *file replication* and *partitioning*, the concurrency challenge is much more complex.

## 3.2 Revisions to Protocol

Concurrency is closely related to system performance. In current protocol, if many clients connect to a single server (e.g., `sunrise.cis.unimelb.edu.au:3781`). The cost of ensuring concurrency could be high. Supporting more advanced features (e.g., *file replication*, *distributed transactions*) can improve the performance.

As for concurrency control. All 'write' commands, i.e., PUBLISH, SHARE, REMOVE, all requires small system resource to process, the possibility of conflict is rather low too, so *optimistic concurrency control* is suitable (Haritsa, Carey, and Livny).

Files stored on the server machine could be manually deleted. Therefore, the resource URI is pointing to an invalid file. We can lock the file to avoid such situation.

# References

Coulouris, George F., Jean Dollimore, and Tim Kindberg. Distributed Systems: Concepts and Design. pearson education, 2005. Print.

Haritsa, Jayant R., Michael J. Carey, and Miron Livny. “Dynamic Real-Time Optimistic Concurrency Control.” Real-Time Systems Symposium, 1990. Proceedings., 11th. IEEE, 1990. 94–103. Print.