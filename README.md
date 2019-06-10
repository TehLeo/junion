(More Information available at this project's [website](https://tehleo.github.io/junion/))
(Current version [1.2.2 EA (Early-Access)](https://github.com/TehLeo/junion/releases))
# Project JUnion
[![Gitter chat](https://badges.gitter.im/gitterHQ/gitter.png)](https://gitter.im/JUnionChat/Lobby)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.tehleo/junion/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.tehleo/junion)

![Class Struct Diagram](docs/drawing.png)

**Delivers struct types to Java programming language.** 

When creating arrays of int, we have two main options:
```java
int[] intArray = new int[1000];  
Integer[] intBoxedArray = new Integer[1000];
```
**How many bytes do** ```intArray, intBoxedArray``` **take to store 1000 ints?**

```intArray``` 4016 bytes ```4*1000 + ~16(around 16 bytes for array header)``` <br>
```intBoxedArray``` 20016 bytes ```(4 + ~12 + ~4)*1000 + ~16``` (exact number depends on VM)

**That is almost 5x more!** <br>
Well, this teaches us to prefer primitive arrays over their boxed versions. <br>
So what is this project about?

Consider
```java
class Point { float x,y;}
Point[] arr = new Point[500];
```
```arr``` takes 14016 bytes <br>
The data consits of 500 points, 2 floats each, thus 4000 bytes should be enough.<br>
If Point was a **struct**, ```arr``` would take ~4000 bytes.

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
Vec3[] arr = new Vec3[10];
arr[5].x = 10;
Vec3 v = arr[5];
...
//
ByteBuffer a = ByteBuffer.allocateDirect(10*Mem.sizeOf(Vec3.class))
   .order(ByteOrder.nativeOrder());
//Modify Direct Native Bytebuffer as it were a struct
Vec3[] arr = Mem.wrap(a, Mem.sizeOf(Vec3.class));
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

## Download

Check out the [latest release](https://github.com/TehLeo/junion/releases)

and usage/IDE integration [guide here.](https://tehleo.github.io/junion/install.html)

## Support & Donations

Would you like to support JUnion? You can report bugs or request for new features [here](https://github.com/TehLeo/junion/issues) or [chat here](https://gitter.im/JUnionChat/Lobby)

Or would you like to make a donation?
You can do so [via PayPap](https://www.paypal.me/JurajPapp)

## News & Info

**Status of JUnion 1.2.2:**

JUnion 1.2.2 EA (Early-Access) is now [available here](https://github.com/TehLeo/junion/releases)

**List of changes:**

- can allocate struct arrays on heap, off-heap, stack, the syntax uses annotation, eg:
```
Vec3[] arr = new Vec3[10]; //default currently allocates on heap
Vec3[] arr = new @Heap Vec3[10];
Vec3[] arr2 = new @Direct Vec3[10];
Vec3[] arr4 = new @DirectBuffer Vec3[10];
Vec3[] arr5 = new @Stack Vec3[10];
Vec3[] arr7 = new @Heap(ArrayType.Byte) Vec3[10]; //underlying data is Java byte[] array
```
- the underlying storage can be retrieved as Java array/Buffer with `Mem.getJavaArray(arr)` and `Mem.getJavaBuffer(arr)`
- added StructType class, which can be used to find information about a struct
- added StructList, which serves as a resizable struct array, it stores its elements as data not as references (not yet finished)
- Array allocation is done through ArrayAllocator interface and can be customized by the user if needed
- Bridge interface serves as the link between the application and ram. The default bridge interface DefaultBridge uses Unsafe to read and write memory. 
- added MemInit class which stores related settings, such as setting the bridge interface, allocator interface to use, etc.

Finishing the current release/Documentation/tests are now planned to finalize 1.2.x release.

Roadmap to JUnion 1.2.x has been announced [here](https://github.com/TehLeo/junion/issues/3) 

Wiki has been created and can be accessed [here](https://tehleo.github.io/junion/wiki/index.html)
