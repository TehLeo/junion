## Struct Types

**Design**

* Ability to define struct types with similar syntax to a class. [Implemented]
* Struct types can not inherit or be inherited from. [Not enforced.]
* ability to use constructors [To be implemented.]
* ability to use non-static methods [To be implemented.]
* struct & field memory alignment [Implemented.]
* no runtime type information [Implemented.]
* no memory overhead for data storage except for alignment [Implemented for structs without Java objects.]
* automatic memory alignment, optionally allow specifying custom memory layout [Implemented.]
* performance similar to primitive types [Implemented.]

**Memory Layout**

Example:
```java
@Struct
public class Vec3 {
    public float x;
    public double y;
    public byte z;
}
```
Vec3 has the following data:
```
0    4        12
|   x|       y|z|
```
(Information about <a target="_blank" href="https://en.wikipedia.org/wiki/Data_structure_alignment">Data Structure Alignment</a>)

In total 13 bytes are required for storage. However, the above representation is not aligned. This is because the double 'y'
starts at index 4. Since double has size of 8 bytes, for double to be aligned its index has to be a multiple of 8. We can
solve the alignment issue without introducing padding by reordering the elements.
```
0        8    12
|       y|   x|z|
```
Now every element is aligned. However, consider the case when we will allocate an array of objects.
```
0        8    12 13      21   25
|       y|   x|z|       y|   x|z|
```
Once again, such allocation would not be aligned, as neither second, 'y' or 'x' is aligned. To solve this we introduce
'end padding'. End padding is calculated as follows: first find the element('x', 'y' or 'z') with the highest alignment
requirement. This is 'y' and it is 8. 8 is the highest allignment requirement. After this add x number of bytes so that
the size of the whole struct is divisible by the highest alignment requirement. We use 13 bytes to store Vec3, thus if we add 3,
the total will be 16 which is divisible by 8. Thus end padding is 3, sizeOf(Vec3) is 16.

This means that if we allocate array of 10 Vec3, they will use 160 bytes. 3 bytes per Vec3 are not utilized to preserve alignment.
Due to the maxiumim alignment requirement of 8, you can lose up to 7 bytes per instance for alignment.
For example the following struct would end up with end padding of 7:
```java
@Struct class SomeStruct {double a; byte b;}
```

Back to the example of Vec3. The end padding for Vec3 was 3 bytes. There are cases where end padding is not needed,
such as the following:

```java
@Struct class StrExample { byte b; char ch; Vec3 vec; }
```
The size of Vec3 without end padding is 13 bytes. StrExample also has a byte and char, thus the total is 16 bytes. After reordering
its layout is as follows:

```
0        8    12 13 15
|       y|   x|z|ch|b|
```
Thus, StrExamples manages to place data in such a way that that the size of StrExample is 16 bytes, and its end padding is 0.

**Syntax & Struct types**

Defining a struct type is similar to defining a class. Struct is distinguished by @Struct annotation. 

Struct type can contain:
 * primitive fields
 * struct type fields
 * references to struct types
 * object fields
 * static fields
 * static methods
 * (Not implemented yet)constructors
 * (Not implemented yet)non-static methods

```java
@Struct 
public class StructExample {
    public static long id; //just like for objects, static memebers are not allocated with struct
    
    //public StructExample itself; <- not allowed, struct cannot contain itself
    @Reference
    public StructExample itself; //<- but can contain reference to itself

    boolean bool; byte byt; char ch; int i; long l;
    public float flt;
    public double dbl;
    
    public Object o; // <- java Object reference
    public String s; // <- java Object reference
    public float[] arr; // <- java Object reference
}
```
Due to the design principle features are optional, structs are divided into two categories.

Structs with no java objects references, and structs with java object references (
these will be reffered to as hybrid structs). Thus 'StructExample' is a hybrid struct
as it contains java Object references.

```java
@Struct 
public class ExHyb {
    public StructExample value;
}
@Struct 
public class ExHyb2 {
    @Reference
    public StructExample value;
}
```
Similarly, structs containg hybrid structs are considered hybrid as well.
Thus 'ExHyb' is a hybrid struct, while 'ExHyb2' is not because it contains a reference only.

The distinction between normal/hybrid structs might be pointed out when talking about a specific feature.

**Array Allocation**

Array allocation follows Java syntax. Allocated struct objects have zeroed memory. No constructors calls are required. Constructors with struct types are not supported in current version. 

It is good to remember that stack allocated objects do not necessairly live till the end of the method. They can be kept alive that long by using their reference at the end of method:

```java

void test() {
    Vec3[] arr = new Vec3[10]; //<- allocates memory, freed if arr is gced
    arr[5].x = 10;
    //arr[-1].x = 10; <- would throw IndexOutOfBounds
    //arr[10].x = 10; <- would throw IndexOutOfBounds
    
    Vec3 v5 = arr[5]; // <- last reference to 'arr'
    for(int i = 0; i < 1000; i++) {
        superLongTask(v5);
    }
    
    Mem.tag(arr); //<- if you comment this line, 'arr' might be gced after line 'Vec3 v5 = arr[5];'
    //Mem.free(arr); <- alternatively you can free the memory instead of waiting for gc to do the job
}
```

**References**

Local references to structs are non-nullable. This is done for performance reason, so that no null pointer exception checks have to be performed.

Field references are nullable, and null-check is performed for them. Instead of throwing NullPointerException, a new exception
NullPointerDereference was created to distinguish between Java null pointer and Struct null pointer, which should facilitate debugging.

```java
Vec3 fieldRef;
void test() {
    Vec3[] arr = new Vec3[10];
    
    Vec3 localRef = arr[5];
    localRef.x = 10; 
    //localRef = null; // <- compile error

    fieldRef = arr[5];
    fieldRef.x = 10; // <- null check performed (another thread might have set fieldRef to null)
    fieldRef = null;
    
    //fieldRef.x = 10; <- throws NullPointerDereference
    //localRef = fieldRef; <- throws NullPointerDereference
    
    //if(fieldRef == null) {}  <- throws NullPointerDereference
    if(Mem.isNull(fieldRef)) {  } //use this instead

    Mem.tag(arr);
}
```
Struct types have no runtime type. References are implemented with 'long' type. This is useful to remember
when creating overloaded methods that take struct type arguments/arrays.

```java
void test1(long a);
void test1(Vec3 a);  //<- compile error, test1(long) already defined
```
Similarly:

```java
void test1(Vec3 a);
void test1(StrExample a);  //<- compile error, test1(long) already defined
```
