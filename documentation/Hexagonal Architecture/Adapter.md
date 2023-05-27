## Usage
An Adapter is created by first creating a class, extending `Adapter<>`. As the generic parameter the appropriate [[Protocol]] is to be provided. The class should also extend the appropriate [[Protocol]].
Alternatively you may define a different class as said above, and use that as a base class for your adapter.
### Example
_TestHelloWorldAdapter.java_
``` java
package org.app.hexagonal.test;  
  
import org.app.hexagonal.Adapter;  
  
public class TestHelloWorldAdapter extends Adapter<IHelloWorldProtocol> implements IHelloWorldProtocol {  
	@Override  
	public void helloWorld() {  
		getPort().helloWorld();  
	}  
}
```
The _IHelloWorldProtocol_ is given as an example in the  [[Protocol]] entry. [Here](obsidian://open?vault=documentation&file=Hexagonal%20Architecture%2FExample) is also a complete example.