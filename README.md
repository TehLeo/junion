(More Information available at this project's [website](https://tehleo.github.io/junion/))
# Project JUnion

**Delivers struct types to Java programming language.** 

Wouldn't it be nice to be able to create struct types in Java that code like class and work like structs?

With JUnion you can do just that by marking a class with @Struct annotation!

**Create struct Vec3:**

```java
@Struct
public class Vec3 {
	public float x,y,z;
}
```

**Afterwards you can use it as:**

```java
//Create a new struct array
Vec3[] arr = Vec3[10];
arr[5].x = 10;
Vec3 v = arr[5];
...
//
ByteBuffer a = ByteBuffer.allocateDirect(10*Mem.sizeOf(Vec3.class))
   .order(ByteOrder.nativeOrder());
//Modify Direct Native Bytebuffer as it were a struct
Vec3 arr = Mem.wrap(a);
arr[5].x = 10;
...
```

For a list of features [click here](https://tehleo.github.io/junion/features.html).

**Why use struct types?**

* Struct types use less memory.
* Have the performance of primitive types.
* Allow you to set data in direct native ByteBuffers with class-like syntax.

**Performance Test**

![alt text](docs/testarrayperf75.png)
