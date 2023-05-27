## Usage
A Port is created by creating a new class which extends the `Port<>` class. As the first generic parameter the appropritate [[Module]] class is to be provided. As the second genetic parameter the appropriate [[Protocol]] is to be provided.
The Class should also implement the appropriate [[Protocol]].
Alternatively you may implement your own Port class, by implementing IPort. This is primarily for ports that don't interface with a module, and may introduce bugs.
### Example
_HelloWorldPort.java_
``` java
package org.app.hexagonal.test;  
  
import org.app.hexagonal.Port;  
  
public class HelloWorldPort extends Port<HelloWorldModule, IHelloWorldProtocol> implements IHelloWorldProtocol {  
  
	@Override  
	public void helloWorld() {  
		getModule().helloWorld();  
	}  
}
```
The _HelloWorldModule_ as well as the _IHelloWorldProtocol_ are given as examples in the [[Module]] and [[Protocol]] entries. [Here](obsidian://open?vault=documentation&file=Hexagonal%20Architecture%2FExample) is also a complete example.