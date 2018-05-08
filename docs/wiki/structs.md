## Struct Types

**Design**

* Ability to define struct types with similar syntax to a class.
* Struct types can not inherit or be inherited from.
* ability to use constructors
* ability to use non-static methods
* struct & field memory alignment
* no runtime type information
* no memory overhead for data storage except for alignment
* automatic memory alignment, optionally allow specifying custom memory layout
* performance similar to primitive types

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
