## Usage
A Protocol is nothing more than a interface. This interface provides all methods for the [[Port]], the [[Adapter]], and sometimes the [[Module]]. For visualization purposes, imagine the Protocol as a USB plug. And all the methods are the pins.
A Protocol is thus used to define new [Ports](obsidian://open?vault=documentation&file=Hexagonal%20Architecture%2FPort) and [Adapters](obsidian://open?vault=documentation&file=Hexagonal%20Architecture%2FAdapter) for said ports.
### Example
*IHelloWorldProtocol.java*
``` java
package org.app.hexagonal.test;  
  
public interface IHelloWorldProtocol {  
	public void helloWorld();  
}
```
[Here](obsidian://open?vault=documentation&file=Hexagonal%20Architecture%2FExample) is also a complete example.