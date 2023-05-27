## Goal
The Hexagonal software architecture strides to achieve a highly modular and skinny Application, where the bulk of systems are swapable.
**DISCLAIMER:** The Hexagonal software architecture used in the project is highly stripped down. Distinguishing factors like Driving and Driven side as well as the strict focus around Domains are gone. This is both to save on complexity and because it isn't required for this particular use case.
## Details
### Port
A [[Port]] is a socket for an [[Adapter]] to plug into and thus provides a way to extend an Application via a unified [[Protocol]], through which, in most cases, communication to a [[Module]] is established.
### Adapter
An [[Adapter]] is plugged into a [[Port]] to establish connection between the application and an external system.
### Protocol
A [[Protocol]] is an interface which defines all methods a [[Port]] as well as an [[Adapter]] should have. Think of it as the contacts in a USB plug and port. In some cases the [[Module]] may 
### Module
A [[Module]] is often used in combination with a [[Port]], to be the System in direct correspondence to the [[Adapter]].
## Further
### HexHelper
HexHelper is a static helper class for managing connections between [Ports](obsidian://open?vault=documentation&file=Hexagonal%20Architecture%2FPort), [Adapters](obsidian://open?vault=documentation&file=Hexagonal%20Architecture%2FAdapter), and [Modules](obsidian://open?vault=documentation&file=Hexagonal%20Architecture%2FModule).