package org.app.ecs;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import static org.app.utils.Logger.*;


@SuppressWarnings("unused")
public class ECS {
    public static final int MAX_ENTITIES = 1000;
    public static final int MAX_COMPONENTS = 1000;

    public interface IComponentArray {
        void entityDestroyed(Entity e);
    }

    public class ComponentArray<T> implements IComponentArray {

        //----- Members -----

        private T[] array;
        private Map<Entity, Integer> entityToIndexMap = new HashMap<>(MAX_ENTITIES);
        private Map<Integer, Entity> indexToEntityMap = new HashMap<>(MAX_ENTITIES);
        int size = 0;

        //----- Methods -----

        public ComponentArray(Class<? extends T> impl) {
            array = (T[]) Array.newInstance(impl, MAX_COMPONENTS);
        }

        public void insertData(Entity e, Object component) {
            if (entityToIndexMap.containsKey(e)) {
                logError("Tried adding same component to entity multiple times");
                return;
            }

            int newIndex = size;
            entityToIndexMap.put(e, newIndex);
            indexToEntityMap.put(newIndex, e);
            array[newIndex] = (T) component;
            ++size;
        }

        public void removeData(Entity e) {
            if (!entityToIndexMap.containsKey(e)) {
                logError("Tried removing component from non-existent entity");
                return;
            }

            int removedIdx = entityToIndexMap.get(e);
            int lastIdx = size - 1;
            array[removedIdx] = array[lastIdx];
            array[removedIdx] = null;

            Entity removed = indexToEntityMap.get(lastIdx);
            entityToIndexMap.put(removed, removedIdx);
            indexToEntityMap.put(removedIdx, removed);

            entityToIndexMap.remove(e);
            indexToEntityMap.remove(lastIdx);

            --size;
        }

        public T getData(Entity e) {
            if (!entityToIndexMap.containsKey(e)) {
                logError("Tried getting data from unregistered entity");
                assert (false);
            }

            return array[entityToIndexMap.get(e)];
        }

        @Override
        public void entityDestroyed(Entity e) {
            if (entityToIndexMap.containsKey(e)) {
                removeData(e);
            }
        }
    }

    public class ComponentManager {

        //----- Members -----

        private Map<String, Integer> componentTypes = new HashMap<>(MAX_ENTITIES);
        private Map<String, IComponentArray> componentArrays = new HashMap<>(MAX_ENTITIES);

        private int nextComponentType = 0;

        //----- Methods -----

        private <T> ComponentArray<T> getComponentArray(Class<? extends T> c) {
            String typeName = c.getSimpleName();

            if (!componentTypes.containsKey(typeName)) {
                logError("Tried to retrieve unregistered component array");
                assert (false);
            }

            return (ComponentArray<T>) componentArrays.get(typeName);
        }

        public <T> void registerComponent(Class<? extends T> c) {
            String typeName = c.getSimpleName();

            if (componentTypes.containsKey(typeName)) {
                logError("Tried to register component type multiple times");
                return;
            }

            componentTypes.put(typeName, nextComponentType);
            componentArrays.put(c.getSimpleName(), new ComponentArray<T>(c));

            nextComponentType++;
        }

        public <T> int getComponentType(Class<? extends T> c) {
            String typeName = c.getSimpleName();

            if (!componentTypes.containsKey(typeName)) {
                logError("Tried to retrieve type of unregistered component type");
                assert (false);
            }

            return componentTypes.get(typeName);
        }

        public <T> void addComponent(Entity e, T c) {
            getComponentArray(c.getClass()).insertData(e, c);
        }

        public <T> void removeComponent(Class<? extends T> c, Entity e) {
            getComponentArray(c).removeData(e);
        }

        public <T> T getComponent(Class<? extends T> c, Entity e) {
            return getComponentArray(c).getData(e);
        }

        public void entityDestroyed(Entity e) {
            for (int i = 0; i < componentArrays.size(); i++) {
                componentArrays.get(i).entityDestroyed(e);
            }
        }
    }

    public class EntityManager {
        //---- Members -----

        private Queue<Entity> availableEntities = new LinkedList<>();
        private Signature[] signatures = new Signature[MAX_ENTITIES];
        private int livingEntityCount = 0;

        //----- Methods -----

        public EntityManager() {
            for (int i = 0; i < MAX_ENTITIES; i++) {
                availableEntities.offer(new Entity(i));
                signatures[i] = new Signature();
            }
        }

        public Entity createEntity() {
            if (livingEntityCount >= MAX_ENTITIES) {
                logError("Tried to create new Entity, when entity limit is reached!");
                assert (false);
            }
            Entity e = availableEntities.poll();
            ++livingEntityCount;
            return e;
        }

        public void destroyEntity(Entity e) {
            if (e.getId() >= MAX_ENTITIES) {
                logError("Tried to delete out-of-range entity: " + e.getId());
                return;
            }

            signatures[e.getId()].clear();

            availableEntities.offer(e);
            --livingEntityCount;
        }

        public void setSignature(Entity e, Signature b) {
            if (e.getId() >= MAX_ENTITIES) {
                logError("Tried to delete out-of-range entity: " + e.getId());
                return;
            }

            signatures[e.getId()] = (Signature) b.clone();
        }

        public Signature getSignature(Entity e) {
            if (e.getId() >= MAX_ENTITIES) {
                logError("Tried to delete out-of-range entity: " + e.getId());
                assert (false);
            }

            return signatures[e.getId()];
        }
    }

    public class SystemManager {
        //----- Members -----

        private Map<String, Signature> signatures = new HashMap<>();
        private Map<String, System> systems = new HashMap<>();

        //----- Methods -----

        public <T> T registerSystem(Class<? extends T> c) {
            String typeName = c.getSimpleName();

            if (systems.containsKey(typeName)) {
                logError("Tried registering same system multiple times");
                assert (false);
            }

            T system;
            try {
                system = c.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            systems.put(typeName, (System) system);
            return system;
        }

        public <T> void setSignature(Signature s, Class<? extends T> c) {
            String typeName = c.getSimpleName();

            if (!systems.containsKey(typeName)) {
                logError("Tried changing signature from unregistered system");
                return;
            }

            signatures.put(typeName, s);
        }

        public void entityDestroyed(Entity e) {
            for (System s : systems.values()) {
                s.entityErased(e);
            }
        }

        public void entitySignatureChanged(Entity e, Signature s) {
            for (Map.Entry<String, System> entry : systems.entrySet()) {
                if (s.compare(signatures.get(entry.getKey()))) {
                    entry.getValue().entities.add(e);
                    entry.getValue().entityRegistered(e);
                } else {
                    entry.getValue().entities.remove(e);
                    if(entry.getValue().entities.contains(e))
                        entry.getValue().entityErased(e);
                }
            }
        }
    }

    public interface IResourceArray {

    }

    public class ResourceArray<T> implements IResourceArray {
        //----- Members -----

        private Map<String, T> data = new HashMap<>();

        //----- Methods -----

        public T getResource(String key) {
            return data.get(key);
        }

        public void setResource(String key, Object value) {
            data.put(key, (T) value);
        }

        public void deleteResource(String key) {
            data.remove(key);
        }

        public void deleteAll() {
            data.clear();
        }
    }

    public class ResourceManager {
        //----- Members -----

        private Map<String, IResourceArray> resourceArrays = new HashMap<>();

        //----- Methods -----

        private <T> ResourceArray<T> getResourceArray(Class<? extends T> c) {
            String typeName = c.getSimpleName();

            if (!resourceArrays.containsKey(typeName)) {
                logError("Tried to retrieve unregistered recource array");
                assert (false);
            }

            return (ResourceArray<T>) resourceArrays.get(typeName);
        }

        public <T> void registerResourceType(Class<? extends T> c) {
            String typeName = c.getSimpleName();
            if (resourceArrays.containsKey(typeName)) {
                logError("Tried to register same recource type multiple times");
                assert (false);
            }

            resourceArrays.put(typeName, (IResourceArray) new ResourceArray<T>());
        }

        public <T> T getResource(String key, Class<? extends T> c) {
            return getResourceArray(c).getResource(key);
        }

        public <T> void setResource(String key, T value) {
            getResourceArray(value.getClass()).setResource(key, value);
        }

        public <T> void deleteResource(String key, Class<? extends T> c) {
            getResourceArray(c).deleteResource(key);
        }

        public void deleteAll() {
            resourceArrays.clear();
        }
    }

    //----- Members -----

    private ComponentManager componentManager;
    private EntityManager entityManager;
    private SystemManager systemManager;
    private ResourceManager resourceManager;

    //----- Methods -----

    public ECS() {
        componentManager = new ComponentManager();
        entityManager = new EntityManager();
        systemManager = new SystemManager();
        resourceManager = new ResourceManager();
        logInfo("ECS initialized");
    }

    //--- Entity Manager ---

    public Entity createEntity() {
        return entityManager.createEntity();
    }

    public void destroyEntity(Entity e) {
        entityManager.destroyEntity(e);
    }

    public void setSignature(Entity e, Signature s) {
        entityManager.setSignature(e, s);
    }

    public Signature getSignature(Entity e) {
        return entityManager.getSignature(e);
    }

    //--- Component Manager ---
    public <T> void registerComponent(Class<? extends T> c) {
        componentManager.registerComponent(c);
    }

    public <T> int getComponentType(Class<? extends T> c) {
        return componentManager.getComponentType(c);
    }

    public <T> void addComponent(Entity e, T c) {
        componentManager.addComponent(e, c);

        Signature sig = entityManager.getSignature(e);
        sig.setBit(componentManager.getComponentType(c.getClass()), true);
        entityManager.setSignature(e, sig);

        systemManager.entitySignatureChanged(e, sig);
    }

    public <T> void removeComponent(Entity e, Class<? extends T> c) {
        componentManager.removeComponent(c, e);

        Signature sig = entityManager.getSignature(e);
        sig.setBit(componentManager.getComponentType(c), false);
        entityManager.setSignature(e, sig);

        systemManager.entitySignatureChanged(e, sig);
    }

    public <T> T getComponent(Class<? extends T> c, Entity e) {
        T temp = componentManager.getComponent(c, e);
        //System.out.println();
        return temp;
    }

    //--- System Manager ---

    public <T> T registerSystem(Class<? extends T> c) {
        return systemManager.registerSystem(c);
    }

    public <T> void setSystemSignature(Signature s, Class<? extends T> c) {
        systemManager.setSignature(s, c);
    }

    public void entitySignatureChanged(Entity e, Signature s) {
        systemManager.entitySignatureChanged(e, s);
    }

    //--- Resource Manager ---

    public <T> void registerResourceType(Class<? extends T> c) {
        resourceManager.registerResourceType(c);
    }

    public <T> T getResource(String key, Class<? extends T> c) {
        return resourceManager.getResource(key, c);
    }

    public <T> void setResource(String key, T value) {
        resourceManager.setResource(key, value);
    }

    public <T> void deleteResource(String key, Class<? extends T> c) {
        resourceManager.deleteResource(key, c);
    }

    public void deleteAllResources() {
        resourceManager.deleteAll();
    }

    //--- General ---

    public void entityDestroyed(Entity e) {
        componentManager.entityDestroyed(e);
        systemManager.entityDestroyed(e);
    }
}