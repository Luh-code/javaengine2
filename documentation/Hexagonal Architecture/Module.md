## Usage
A Module is created my creating a new class which is going to be directly connected to the [[Port]]. You may implement the appropriate [[Protocol]] even though this is not necessary. 
### Example
*HelloWorldModule.java*
``` java
package org.app.hexagonal.test;  
  
public class HelloWorldModule {  
	public void helloWorld()  
	{  
		System.out.println("Hello World!!!");  
	}  
}
```
[Here](obsidian://open?vault=documentation&file=Hexagonal%20Architecture%2FExample) is also a complete example.