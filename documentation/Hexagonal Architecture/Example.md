## Implementation
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
*IHelloWorldProtocol.java*
``` java
package org.app.hexagonal.test;  
  
wpublic interface IHelloWorldProtocol {  
	public void helloWorld();  
}
```
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

### Entry Point
*Main.java*
``` java
package org.app.hexagonal.test;  
  
import org.app.hexagonal.HexHelper;  
  
public class Main {  
	public static void main(String[] args) {  
		HelloWorldPort port = new HelloWorldPort();  
		HelloWorldModule module = new HelloWorldModule();  
		TestHelloWorldAdapter adapter = new TestHelloWorldAdapter();  
		HexHelper.connect_module(port, module);  
		HexHelper.connect(port, adapter);  
		  
		adapter.helloWorld();  
	}  
}
```