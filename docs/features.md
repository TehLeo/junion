## Features

#### Struct Types

Struct types lets you define data types which use as little memory as possible.

You can define a struct type by annotating it with @Struct  annotation.

```java
@Struct
public class Vec3 {
	public float x,y,z;
}
```
Struct Vec3 has a size of 12 bytes. You can double check this by running:

```java
System.out.println("Size: " + Mem.sizeOf(Vec3.class));
System.out.println(Mem.layoutString(Vec3.class));
```
**Output**
```
Size: 12
~Struct test.HelloStruct$Vec3(12/12/12) Align: 4 ~
|0:x FLOAT(4), -1|
|4:y FLOAT(4), -1|
|8:z FLOAT(4), -1|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~
```

#### Automatic data alignment

For information about data alignment [see](https://en.wikipedia.org/wiki/Data_structure_alignment).

```java
@Struct
public class MixedData {
	byte Data1;
    short Data2;
    int Data3;
    byte Data4;
}
```
Struct MixedData does not have its data aligned. JUnion detects this and realigns the data automatically. We can once again use the following to check the struct size and layout.

```java
System.out.println("Size: " + Mem.sizeOf(MixedData.class));
System.out.println(Mem.layoutString(MixedData.class));
```

**Output**
```
Size: 8
~Struct test.HelloStruct$MixedData(8/8/8) Align: 4 ~
|0:Data3 INT(4), -1|
|4:Data2 SHORT(2), -1|
|6:Data1 BYTE(1), -1|
|7:Data4 BYTE(1), -1|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
```

The Data3 stars at index 0 which is a multiple of four, thus is aligned. Similarly Data2 starts at index 4 which is a multiple of 2, being aligned as well.

#### Manual data alignment

If your code depends on the layout of data within a struct, use the annotation property autopad.

```java
@Struct(autopad=false)
public class Manual {
    public byte b;
    private byte padding;
    public char ch;
}
```
By setting autopad to false, you are required to define your data so that it is aligned.
The compiler will throw an error if a structure is not properly aligned.

#### Creating Arrays of Struct Types

Struct arrays are allocated on the heap and are automatically freed.

```java
Vec3[] arr = new Vec3[12];
arr[5].y = 10;
		
System.out.println("arr[5].y = " + arr[5].y );
```

#### 64-bit long addressable arrays

Struct arrays support addressing with longs.
```java
Vec3[] arr = new Vec3[Mem.li(1000000000L)];
arr[Mem.li(900000000L)].y = 10;
		
System.out.println("y = " + arr[Mem.li(900000000L)].y );
```


#### Modifying Native DirectByteBuffers

Not only is accessing/modifying bytebuffers more readable with struct syntax, it also improves performance.

```java
ByteBuffer a = ByteBuffer.allocateDirect(num*vec3Size).order(ByteOrder.nativeOrder());

Vec3[] av = Mem.wrap(a, Mem.sizeOf(Vec3.class));

```

#### Index Checking

```java
Vec3[] arr = new Vec3[12];

arr[-1].x = 5; //throws IndexOutOfBoundsException
arr[12].x = 5; //throws IndexOutOfBoundsException
```

#### Nested Structures


```java
@Struct
public class Line2 {
	public Vec3 a, b;
}
```

**Layout**
```
Size: 24
~Struct test.HelloStruct$Line2(24/24/24) Align: 4 ~
|0:a STRUCT(4), -1|
|12:b STRUCT(4), -1|
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
```


#### Structure References

When building tree structures you may write something like.

```java
@Struct
public class Node {
    public Node left, right;
}
```

However, a structure cannot contain itself. JUnion detects this error as a Circular Dependence.

To build tree structures we can write:
```java
@Struct
public class Node {
    @Reference public Node left, right;
}
```

#### Null Reference Checking

```java
Node[] n = new Node[10];
Node a = n[0].left; //throws NullPointerDereference
```
To check if a reference is null, use Mem.isNull

```java
Node[] n = new Node[10];
if(!Mem.isNull(n[0].left)) {
	Node a = n[0].left;
}
```

#### Array Slices

You can create array slices. Slices share data, thus changes in slice are reflected in the original array.

```java
Vec3[] arr = new Vec3[10];
for(int i = 0; i < arr.length; i++) arr[i].x = i;
		
Vec3[] a = Mem.slice(arr, 5, arr.length);
Vec3[] b = Mem.slice(arr, 5, arr.length, 2);
Vec3[] reversed = Mem.slice(arr, 0, arr.length, -1);
		
System.out.print("a:\t");
for(int i = 0; i < a.length; i++) System.out.print(a[i].x + ",");
System.out.print("\nb:\t");
for(int i = 0; i < b.length; i++) System.out.print(b[i].x+",");
System.out.print("\nrev:\t");
for(int i = 0; i < reversed.length; i++) System.out.print(reversed[i].x+",");
```
**Output**
```
a:	    5.0,6.0,7.0,8.0,9.0,
b:      5.0,7.0,
rev:	9.0,8.0,7.0,6.0,5.0,4.0,3.0,2.0,1.0,0.0,
```


