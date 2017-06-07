package cc.zgeek.pagelib.Utils

/**
 * Created by ZGeek on 2017/5/23.
 */

import kotlin.reflect.KProperty


public fun <T> myLazy(initializer: () -> T, whenInitialized: (obj:T) -> Unit): Lazy<T> = MySynchronizedLazyImpl(initializer, whenInitialized)


/**
 * An extension to delegate a read-only property of type [T] to an instance of [Lazy].
 *
 * This extension allows to use instances of Lazy for property delegation:
 * `val property: String by lazy { initializer }`
 */

public inline operator fun <T> Lazy<T>.getValue(thisRef: Any?, property: KProperty<*>): T = value

/**
 * Specifies how a [Lazy] instance synchronizes access among multiple threads.
 */
public enum class LazyThreadSafetyMode {

    /**
     * Locks are used to ensure that only a single thread can initialize the [Lazy] instance.
     */
    SYNCHRONIZED,

    /**
     * Initializer function can be called several times on concurrent access to uninitialized [Lazy] instance value,
     * but only first returned value will be used as the value of [Lazy] instance.
     */
    PUBLICATION,

    /**
     * No locks are used to synchronize the access to the [Lazy] instance value; if the instance is accessed from multiple threads, its behavior is undefined.
     *
     * This mode should be used only when high performance is crucial and the [Lazy] instance is guaranteed never to be initialized from more than one thread.
     */
    NONE,
}


private object UNINITIALIZED_VALUE

private class MySynchronizedLazyImpl<out T>(initializer: () -> T, whenInitialized: (obj:T) -> Unit) : Lazy<T>, java.io.Serializable {
    private var initializer: (() -> T)? = initializer
    private var whenInitialized: ((obj:T) -> Unit)? = whenInitialized
    @Volatile private var _value: Any? = UNINITIALIZED_VALUE
    private val lock = this

    override val value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST")
                return _v1 as T
            }

            return synchronized(lock) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED_VALUE) {
                    @Suppress("UNCHECKED_CAST") (_v2 as T)
                } else {
                    val typedValue = checkNotNull(initializer)()
                    _value = typedValue
                    initializer = null
                    whenInitialized?.invoke(typedValue)
                    whenInitialized = null
                    typedValue
                }
            }
        }

    override fun isInitialized(): Boolean = _value !== UNINITIALIZED_VALUE

    override fun toString(): String = if (isInitialized()) value.toString() else "Lazy value not initialized yet."

    private fun writeReplace(): Any = InitializedLazyImpl(value)
}


private class InitializedLazyImpl<out T>(override val value: T) : Lazy<T>, java.io.Serializable {

    override fun isInitialized(): Boolean = true

    override fun toString(): String = value.toString()

}
