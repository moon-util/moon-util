package com.moon.core.enums;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

import static com.moon.core.lang.ObjectUtil.defaultIfNull;
import static com.moon.core.util.FilterUtil.nullableFirst;

/**
 * 这个类列出了常用集合、队列等，包括特性介绍、关键点的实现以及有限的能测试
 * 结论（部分）：
 * List：{@link #ArrayList}{@link #LinkedList}
 * |- List 是一个有序的集合，可用索引随机访问元素，但不同实现类的随机访问性能差异较大
 * |- 如果不是为了使用 LinkedList 特有的特性（如：FIFO、LIFO）均推荐使用{@link ArrayList}
 * |- List 集合的迭代
 * |- |- 综合而言任何情况下首选 List 自身的 forEach 方法，其次是 iterator 方式
 * |- |- 这两种迭代方式在于不同实现均会对其迭代方式做优化
 * |- |- for 循环根据情况考虑使用
 * |- |- {@link com.moon.core.util.IteratorUtil} 此类的方法已根据情况选择最佳迭代方式，且屏蔽{@link NullPointerException}
 * |
 * Set：{@link #TreeSet}{@link #HashSet}{@link #LinkedHashSet}
 * <p>
 * 注意：{@link #ArrayBlockingQueue}.getSheet() 方法会直接抛出异常
 *
 * @author benshaoye
 * @date 2018/9/11
 */
public enum Collects implements Supplier<Collection>,
    IntFunction<Collection>, Function<Collection, Collection> {

    /*
     * ----------------------------------------------------------------------------
     * List
     * ----------------------------------------------------------------------------
     */

    /**
     * ArrayList 是基于数组实现的集合
     * 内部用数组保存所有集合项，初始容量为 10。也可自定义初始容量，最大容量为 Integer.MAX_VALUE - 8
     * 主动自定义初始容量在一定情况下有助于提升性能
     * 每次添加（increment）、获取（getSheet）、删除（remove）、插入（insert）等操作均会检查位置或容量时候足够
     * - 不够的情况会进行扩容，每次扩容大小为上一次容量的 1.5 倍
     * - {@link ArrayList#grow(int)}扩容具体执行方法
     * - {@link ArrayList#ensureCapacity(int)} 一次性扩容至指定长度
     * - 扩容会影响性能，故推荐指定初始话大小
     * <p>
     * 迭代：
     * ArrayList 的各种迭代方式效率区别并不大，不会出现指数级或者断崖式的差异，
     * 但是不同迭代仍然有微小差异，下面列出一个测试结果（ms, size=100）
     * ----------------------------------------------------------------------
     * | 次数(万)     | 1   | 10  | 100  | 1000  | 10000  | 100000  | 200000 |
     * | for 循环     | 1.0 | 1.0 | 3.0  | 12.0  | 136.0  | 1254.0  | 2504.0 |
     * | foreach     | 1.0 | 1.0 | 4.0  | 17.0  | 183.0  | 1773.0  | 3547.0 |
     * | iterator()  | 1.0 | 1.0 | 4.0  | 25.0  | 254.0  | 2504.0  | 5007.0 |
     * ----------------------------------------------------------------------
     * <p>
     * 支持随机访问，
     * 添加、获取性能均高，但注意操作带来的扩容影响效率
     * 插入或删除效率较低，因为每次插入或删除均会有数据移动操作（除非发生在尾部）
     * <p>
     * 比较：
     * {@link #Vector}
     * <p>
     * 继承结构：
     *
     * @see java.io.Serializable
     * @see Iterable
     * @see Collection
     * @see List
     * @see RandomAccess
     * @see AbstractCollection
     * @see AbstractList
     */
    ArrayList(ArrayList.class) {
        @Override
        public ArrayList apply(Collection collection) {
            return new ArrayList(collection);
        }

        @Override
        public ArrayList apply(int initCapacity) {
            return new ArrayList(initCapacity);
        }

        @Override
        public ArrayList get() {
            return new ArrayList();
        }
    },
    /**
     * LinkedList 是基于双向链表实现的集合
     * 事实上它还可实现队列（FIFO）、栈（LIFO）、双向队列 等功能（LinkedHashMap 可实现 LRU 功能）
     * LinkedList 是一个添加、插入、删除速度均非常优秀的集合，但随机获取的性能比较差
     * 故实际上 LinkedList 的插入和删除性能均一般，因为这两个操作之前均会执行一次随机访问
     * 由于 LinkedList 是双向队列，在实现随机访问中做了一个小优化：
     * - 判断访问位置是在集合的前半部分还是后半部分{@link LinkedList#node(int)}，
     * - 然后分别前头部或尾部开始遍历
     * <p>
     * 迭代：
     * LinkedList 的各种迭代方式效率有较为明显的差异，这主要是由于其链表结构造成的
     * 但是不同迭代仍然有微小差异，下面列出一个测试结果（ms, size=100）
     * --------------------------------------------------------------------------
     * | 次数(万)     | 1    | 10   | 100  | 1000  | 10000  | 1000000 | 2000000  |
     * | for 循环     | 10.0 | 106  | 1025 | 10227 | 102388 | 1024562 | 没跑完    |
     * | foreach     | 3.0  | 10.0 | 76.0 | 758.0 | 7643.0 | 76833.0 | 152356.0 |
     * | iterator()  | 3.0  | 9.0  | 75.0 | 756.0 | 7588.0 | 75562.0 | 152349.0 |
     * --------------------------------------------------------------------------
     * <p>
     * 继承结构：
     *
     * @see Iterable 所有集合的祖宗
     * - {@link Iterable#iterator()}
     * - {@link Iterable#forEach(Consumer)}
     * - {@link Iterable#spliterator()}
     * @see Collection extends {@link Iterable}
     * - {@link Collection#add(Object)}
     * - {@link Collection#addAll(Collection)}
     * - {@link Collection#remove(Object)} 删除指定项
     * - {@link Collection#removeIf(Predicate)} 删除符合条件的
     * - {@link Collection#removeAll(Collection)} 删除所有
     * - {@link Collection#toArray()}
     * - {@link Collection#toArray(Object[])} 转换为指定类型数组
     * - {@link Collection#contains(Object)}
     * - {@link Collection#containsAll(Collection)}
     * - {@link Collection#size()}
     * - {@link Collection#clear()}
     * - {@link Collection#isEmpty()}
     * - {@link Collection#retainAll(Collection)}
     * - {@link Collection#stream()}
     * @see List extends {@link Collection}
     * - {@link List#indexOf(Object)}
     * - {@link List#lastIndexOf(Object)}
     * - {@link List#sort(Comparator)} 排序
     * - {@link List#remove(int)} 删除指定下标元素
     * - {@link List#listIterator()} 迭代器
     * - {@link List#listIterator(int)} 从指定位置开始的迭代器
     * - {@link List#subList(int, int)}
     * - {@link List#parallelStream()}
     * @see Queue extends {@link List}
     * - {@link Queue#remove()} 删除
     * - {@link Queue#peek()}
     * - {@link Queue#poll()}
     * - {@link Queue#offer(Object)} 尾部追加 + return false
     * - {@link Queue#element()}
     * @see Deque extends {@link Queue}
     * - {@link Deque#addFirst(Object)} 头部追加 + 抛出异常
     * - {@link Deque#addLast(Object)} 尾部追加 + 抛出异常
     * - {@link Deque#offerFirst(Object)} 头部追加 + return false
     * - {@link Deque#offerLast(Object)} 尾部追加 + return false
     * - {@link Deque#peekFirst()}
     * - {@link Deque#peekLast()}
     * - {@link Deque#pollFirst()}
     * - {@link Deque#pollLast()}
     * - {@link Deque#push(Object)}
     * - {@link Deque#pop()}
     * @see AbstractList
     * @see AbstractSequentialList
     * @see java.io.Serializable
     * @see Cloneable
     * <p>
     * LinkedList 并未继承此类，但可实现此功能
     * @see Stack
     */
    LinkedList(LinkedList.class) {
        @Override
        public LinkedList apply(Collection collection) {
            return new LinkedList(collection);
        }

        @Override
        public LinkedList apply(int initCapacity) {
            return get();
        }

        @Override
        public LinkedList get() {
            return new LinkedList();
        }
    },

    /**
     * Vector 与 ArrayList 有相同的继承关系，但是是一个线程安全的集合
     * 两者的区别主要有三点：
     * 1、Vector 所有读写方法均加了关键字 synchronized，保证了多线程下数据的安全性
     * 2、Vector 有自己独有的对外接口：element，功能与 increment、remove 等基本一致
     * - 这些接口同样也加了关键字 synchronized 保证数据安全
     * 3、Vector 的扩容方式与 ArrayList 不同：
     * - ArrayList 每次扩容为员容量的 1.5 倍；
     * - Vector 可指定每次扩容大小，或扩容为原容量的 2 倍；
     * <p>
     * 继承结构：(Vector 与 ArrayList 有相同的继承关系)
     *
     * @see java.io.Serializable
     * @see Iterable
     * @see Collection
     * @see List
     * @see RandomAccess
     * @see AbstractCollection
     * @see AbstractList
     */
    Vector(Vector.class) {
        @Override
        public Vector get() {
            return new Vector();
        }

        @Override
        public Vector apply(int size) {
            return new Vector(size);
        }

        @Override
        public Vector apply(Collection collection) {
            return new Vector(collection);
        }
    },


    /**
     * Stack 直接继承自 Vector，同样是一个线程安全的集合
     * Stack 作为 “栈” 的数据结构，有自己的公共接口来保证 FILO 特性：
     * - {@link Stack#peek()}
     * - {@link Stack#pop()}
     * - {@link Stack#push(Object)}
     * - {@link Stack#empty()}
     * 继承结构：(Stack 直接继承自 Vector，故具有与 Vector 相同的结构)
     *
     * @see java.io.Serializable
     * @see Iterable
     * @see Collection
     * @see List
     * @see RandomAccess
     * @see AbstractCollection
     * @see AbstractList
     * @see Vector
     */
    Stack(Stack.class) {
        @Override
        public Stack get() {
            return new Stack();
        }

        @Override
        public Stack apply(int value) {
            return get();
        }

        @Override
        public Stack apply(Collection collection) {
            Stack c = get();
            c.addAll(collection);
            return c;
        }
    },

    /*
     * ----------------------------------------------------------------------------
     * Set
     * ----------------------------------------------------------------------------
     */

    /**
     * TreeSet 是基于 TreeMap 实现的，详见：{@link TreeMap}、{@link Maps#TreeMap}
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see Set
     * @see SortedSet
     * @see NavigableSet
     * @see Cloneable
     * @see java.io.Serializable
     * @see AbstractCollection
     * @see AbstractSet
     */
    TreeSet(TreeSet.class) {
        @Override
        public TreeSet apply(Collection collection) {
            return new TreeSet(collection);
        }

        @Override
        public TreeSet apply(int initCapacity) {
            return new TreeSet();
        }

        @Override
        public TreeSet get() {
            return new TreeSet();
        }
    },
    /**
     * HashSet 基于散列表 {@link HashMap} 实现的
     * HashSet 是一个高效的集合，它通过 hashCode 和 equals 维护一个无序不重复的集合
     * 关系：
     * - HashSet 里的每一项是 HashMap 的键
     * - 所有键对应的值都指向同一个对象 {@link HashSet#PRESENT}
     * - {@link HashMap#put(Object, Object)}
     * - {@link HashSet#add(Object)}
     * <p>
     * - 实际上 HashSet 自身也能实现一个有序的集合{@link HashSet#(int, float, boolean)}
     * > 这个构造器中的第三个参数 dummy，并没有任何实际作用，
     * &nbsp;  只是用来标记通过此构造方法得到的是一个用{@link LinkedHashMap}维护数据而不是{@link HashMap}
     * &nbsp;  但这个构造器是用 default 修饰的，无法被外界调用，
     * &nbsp;  详见：{@link LinkedHashSet}、{@link #LinkedHashSet}、{@link Maps#LinkedHashMap}
     * <p>
     * 详解 {@link HashMap}
     * <p>
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see Set
     * @see AbstractCollection
     * @see AbstractSet
     * @see Cloneable
     * @see java.io.Serializable
     */
    HashSet(HashSet.class) {
        @Override
        public HashSet get() {
            return new HashSet();
        }

        @Override
        public HashSet apply(int initCapacity) {
            return new HashSet(initCapacity);
        }

        @Override
        public HashSet apply(Collection collection) {
            return new HashSet(collection);
        }
    },
    /**
     * 基于 {@link LinkedHashMap} 实现
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see Set
     * @see AbstractCollection
     * @see AbstractSet
     * @see HashSet
     * @see Cloneable
     * @see java.io.Serializable
     */
    LinkedHashSet(LinkedHashSet.class) {
        @Override
        public LinkedHashSet apply(Collection collection) {
            return new LinkedHashSet(collection);
        }

        @Override
        public LinkedHashSet apply(int initCapacity) {
            return new LinkedHashSet(initCapacity);
        }

        @Override
        public LinkedHashSet get() {
            return new LinkedHashSet();
        }
    },

    /*
     * ----------------------------------------------------------------------------
     * Deque
     * ----------------------------------------------------------------------------
     */

    /**
     * 继承结构
     *
     * @see java.io.Serializable
     * @see Iterable
     * @see Collection
     * @see Queue
     * @see Deque
     * @see AbstractCollection
     */
    ArrayDeque(ArrayDeque.class) {
        @Override
        public ArrayDeque get() {
            return new ArrayDeque();
        }

        @Override
        public ArrayDeque apply(int value) {
            return new ArrayDeque(value);
        }

        @Override
        public ArrayDeque apply(Collection collection) {
            return new ArrayDeque(collection);
        }
    },

    /**
     * 继承结构
     *
     * @see java.io.Serializable
     * @see Iterable
     * @see Collection
     * @see Queue
     * @see Deque
     * @see BlockingQueue
     * @see java.util.concurrent.BlockingDeque
     * @see AbstractQueue
     */
    LinkedBlockingDeque(LinkedBlockingDeque.class) {
        @Override
        public LinkedBlockingDeque get() {
            return new LinkedBlockingDeque();
        }

        @Override
        public LinkedBlockingDeque apply(int value) {
            return new LinkedBlockingDeque(value);
        }

        @Override
        public LinkedBlockingDeque apply(Collection collection) {
            return new LinkedBlockingDeque(collection);
        }
    },

    /*
     * ----------------------------------------------------------------------------
     * Queue
     * ----------------------------------------------------------------------------
     */

    /**
     * 继承结构
     *
     * @see Iterable
     * @see Collection
     * @see Queue
     * @see AbstractCollection
     * @see AbstractQueue
     * @see java.io.Serializable
     */
    PriorityQueue(PriorityQueue.class) {
        @Override
        public PriorityQueue get() {
            return new PriorityQueue();
        }

        @Override
        public PriorityQueue apply(int value) {
            return new PriorityQueue(value);
        }

        @Override
        public PriorityQueue apply(Collection collection) {
            return new PriorityQueue(collection);
        }
    },

    /**
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see AbstractCollection
     * @see AbstractQueue
     * @see BlockingQueue
     * @see java.io.Serializable
     */
    LinkedBlockingQueue(LinkedBlockingQueue.class) {
        @Override
        public LinkedBlockingQueue get() {
            return new LinkedBlockingQueue();
        }

        @Override
        public LinkedBlockingQueue apply(int value) {
            return new LinkedBlockingQueue(value);
        }

        @Override
        public LinkedBlockingQueue apply(Collection collection) {
            return new LinkedBlockingQueue(collection);
        }
    },

    /**
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see AbstractCollection
     * @see AbstractQueue
     * @see BlockingQueue
     * @see java.io.Serializable
     */
    ArrayBlockingQueue(ArrayBlockingQueue.class) {
        @Override
        public ArrayBlockingQueue get() {
            throw new UnsupportedOperationException();
        }

        @Override
        public ArrayBlockingQueue apply(int value) {
            return new ArrayBlockingQueue(value);
        }

        @Override
        public ArrayBlockingQueue apply(Collection collection) {
            if (collection == null) {
                return new ArrayBlockingQueue(16);
            } else {
                ArrayBlockingQueue collect = new ArrayBlockingQueue<>(collection.size());
                collect.addAll(collection);
                return collect;
            }
        }
    },

    /**
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see AbstractCollection
     * @see AbstractQueue
     * @see BlockingQueue
     * @see java.io.Serializable
     */
    PriorityBlockingQueue(PriorityBlockingQueue.class) {
        @Override
        public PriorityBlockingQueue get() {
            return new PriorityBlockingQueue();
        }

        @Override
        public PriorityBlockingQueue apply(int value) {
            return new PriorityBlockingQueue(value);
        }

        @Override
        public PriorityBlockingQueue apply(Collection collection) {
            return new PriorityBlockingQueue(collection);
        }
    },

    /**
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see AbstractCollection
     * @see AbstractQueue
     * @see BlockingQueue
     * @see java.io.Serializable
     */
    SynchronousQueue(SynchronousQueue.class) {
        @Override
        public SynchronousQueue get() {
            return new SynchronousQueue();
        }

        @Override
        public SynchronousQueue apply(int value) {
            return get();
        }

        @Override
        public SynchronousQueue apply(Collection collection) {
            return get();
        }
    },

    /**
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see AbstractCollection
     * @see Queue
     * @see AbstractQueue
     * @see BlockingQueue
     * @see TransferQueue
     * @see java.io.Serializable
     */
    LinkedTransferQueue(LinkedTransferQueue.class) {
        @Override
        public LinkedTransferQueue get() {
            return new LinkedTransferQueue();
        }

        @Override
        public LinkedTransferQueue apply(int value) {
            return get();
        }

        @Override
        public LinkedTransferQueue apply(Collection collection) {
            return new LinkedTransferQueue(collection);
        }
    },

    /**
     * 继承结构：
     *
     * @see Iterable
     * @see Collection
     * @see AbstractCollection
     * @see Queue
     * @see AbstractQueue
     * @see java.io.Serializable
     */
    ConcurrentLinkedQueue(ConcurrentLinkedQueue.class) {
        @Override
        public ConcurrentLinkedQueue get() {
            return new ConcurrentLinkedQueue();
        }

        @Override
        public ConcurrentLinkedQueue apply(int value) {
            return new ConcurrentLinkedQueue();
        }

        @Override
        public ConcurrentLinkedQueue apply(Collection collection) {
            return new ConcurrentLinkedQueue(collection);
        }
    };

    static final class CtorCached {
        final static HashMap<Class, Collects> CACHE = new HashMap();
    }

    private final Class type;

    Collects(Class type) {
        CtorCached.CACHE.put(this.type = type, this);
    }

    public Class type() {
        return type;
    }

    /**
     * 从集合类名获取映射，不存在返回 null
     *
     * @param type 集合类
     * @return
     */
    public static Collects get(Class type) {
        return CtorCached.CACHE.get(type);
    }

    /**
     * 从对象获取映射，不存在返回 null
     *
     * @param object 集合对象
     * @return
     */
    public static Collects get(Object object) {
        return object == null ? null : get(object.getClass());
    }

    /**
     * 从集合类名获取映射，不存在返回 null
     * 会追溯超类直至 Object.class 为止返回 null
     *
     * @param type
     * @return
     */
    public static Collects getAsSuper(Class type) {
        for (Collects collect;
             type != null;
             type = type.getSuperclass()) {
            if ((collect = get(type)) != null) {
                return collect;
            }
        }
        return null;
    }

    /**
     * 从对象获取映射，不存在返回 null
     * 会追溯超类直至 Object.class 为止返回 null
     *
     * @param object
     * @return
     */
    public static Collects getAsSuper(Object object) {
        return object == null ? null : getAsSuper(object.getClass());
    }

    /**
     * 从集合类名获取映射，不存在返回 defaultType
     *
     * @param type        集合类
     * @param defaultType 默认值
     * @return
     */
    public static Collects getOrDefault(Class type, Collects defaultType) {
        return CtorCached.CACHE.getOrDefault(type, defaultType);
    }

    /**
     * 从对象获取映射，不存在返回 defaultType
     *
     * @param object      集合对象
     * @param defaultType 默认值
     * @return
     */
    public static Collects getOrDefault(Object object, Collects defaultType) {
        return object == null ? defaultType : getOrDefault(object.getClass(), defaultType);
    }

    /**
     * 从集合类名获取映射，不存在返回 defaultType
     * 此方法会一直追溯集合类的超类，直至 Object.class 为止返回 defaultType
     *
     * @param type        集合类
     * @param defaultType 默认值
     * @return
     */
    public static Collects getAsSuperOrDefault(Class type, Collects defaultType) {
        for (Collects collect; type != null; type = type.getSuperclass()) {
            if ((collect = get(type)) != null) {
                return collect;
            }
        }
        return defaultType;
    }

    /**
     * 从对象获取映射，不存在返回 defaultType
     * 此方法会一直追溯对象的超类，直至 Object.class 为止返回 defaultType
     *
     * @param object      集合对象
     * @param defaultType 默认值
     * @return
     */
    public static Collects getAsSuperOrDefault(Object object, Collects defaultType) {
        return object == null ? defaultType : getAsSuperOrDefault(object.getClass(), defaultType);
    }

    /**
     * 可以自动推断
     *
     * @param type
     * @return
     */
    public static Collects getAsDeduce(Class type) {
        Collects collect = getAsSuper(type);
        if (collect == null && type != null) {
            collect = nullableFirst(values(), item -> item.type().isAssignableFrom(type));
            return collect != null ? collect : (List.class.isAssignableFrom(type) ? ArrayList : HashSet);
        }
        return collect;
    }

    /**
     * 可以自动推断
     *
     * @param object
     * @return
     */
    public static Collects getAsDeduce(Object object) {
        Collects collect = getAsSuper(object);
        if (collect == null && object != null) {
            collect = nullableFirst(values(), item -> item.type().isInstance(object));
            return (collect != null) ? collect : (object instanceof List ? ArrayList : HashSet);
        }
        return collect;
    }

    /**
     * 可以自动推断
     *
     * @param object
     * @return
     */
    public static Collects getAsDeduceOrDefault(Object object, Collects type) {
        Collects collect = getAsSuper(object);
        if (collect == null && object != null) {
            return defaultIfNull(nullableFirst(Collects.values(),
                item -> item.type().isInstance(object)), type);
        }
        return collect;
    }
}