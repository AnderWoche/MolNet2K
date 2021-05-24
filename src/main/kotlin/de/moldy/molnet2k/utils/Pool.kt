package de.moldy.molnet2k.utils

abstract class Pool<T : Any>(initialCapacity: Int, private val max: Int, preFill: Boolean) {

    /** The highest number of free objects. Can be reset any time.  */
    var peak = 0

    private var freeObjects = ArrayList<T>(initialCapacity)

    /** @param initialCapacity The initial size of the array supporting the pool. No objects are created unless preFill is true.
     * @param max The maximum number of free objects to store in this pool.
     * @param preFill Whether to pre-fill the pool with objects. The number of pre-filled objects will be equal to the initial
     * capacity.
     */
    init {
        require(!(initialCapacity > max && preFill)) { "max must be larger than initialCapacity if preFill is set to true." }
        if (preFill) {
            for (i in 0 until initialCapacity) this.freeObjects.add(this.newObject())
            this.peak = this.freeObjects.size
        }
    }

    /** Creates a pool with an initial capacity of 16 and no maximum.  */
    constructor(): this(16, Int.MAX_VALUE, false)

    /** Creates a pool with the specified initial capacity and no maximum.  */
    constructor(initialCapacity: Int): this(initialCapacity, Int.MAX_VALUE, false)

    /** @param max The maximum number of free objects to store in this pool.
     */
    constructor(initialCapacity: Int, max: Int): this(initialCapacity, max, false)

    protected abstract fun newObject(): T

    /** Returns an object from this pool. The object may be new (from [.newObject]) or reused (previously
     * [freed][.free]).  */
    fun obtain(): T {
        return if (this.freeObjects.size == 0) newObject() else this.freeObjects.first()
    }

    /** Puts the specified object in the pool, making it eligible to be returned by [.obtain]. If the pool already contains
     * [.max] free objects, the specified object is reset but not added to the pool.
     *
     *
     * The pool does not check if an object is already freed, so the same object must not be freed multiple times.  */
    fun free(any: T) {
        if (this.freeObjects.size < this.max) {
            this.freeObjects.add(any)
            this.peak = this.peak.coerceAtLeast(this.freeObjects.size)
        }
        reset(any)
    }

    /** Adds the specified number of new free objects to the pool. Usually called early on as a pre-allocation mechanism but can be
     * used at any time.
     *
     * @param size the number of objects to be added
     */
    fun fill(size: Int) {
        for (i in 0 until size) if (this.freeObjects.size < max) this.freeObjects.add(this.newObject())
        this.peak = this.peak.coerceAtLeast(this.freeObjects.size)
    }

    /** Called when an object is freed to clear the state of the object for possible later reuse. The default implementation calls
     * [Poolable.reset] if the object is [Poolable].  */
    internal fun reset(any: T) {
        if (any is Poolable) (any as Poolable).reset()
    }

    /** Puts the specified objects in the pool. Null objects within the array are silently ignored.
     *
     *
     * The pool does not check if an object is already freed, so the same object must not be freed multiple times.
     * @see .free
     */
    fun freeAll(objects: Array<T>) {
        val freeObjects = this.freeObjects
        val max = max
        for (element in objects) {
            if (freeObjects.size < max) freeObjects.add(element)
            reset(element)
        }
        this.peak = this.peak.coerceAtLeast(freeObjects.size)
    }

    /** Removes all free objects from this pool.  */
    fun clear() {
        this.freeObjects.clear()
    }

    /** The number of objects available to be obtained.  */
    fun getFree(): Int {
        return this.freeObjects.size
    }

    /** Objects implementing this interface will have [.reset] called when passed to [Pool.free].  */
    interface Poolable {
        /** Resets the object for reuse. Object references should be nulled and fields may be set to default values.  */
        fun reset()
    }

}