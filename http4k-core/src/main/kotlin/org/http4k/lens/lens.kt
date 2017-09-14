package org.http4k.lens

/**
 * A Lens provides the uni-directional extraction of an entity from a target.
 */
open class Lens<in IN, out FINAL>(val meta: Meta,
                                  private val lensGet: (IN) -> FINAL) : LensExtractor<IN, FINAL>, Iterable<Meta> {
    override fun iterator(): Iterator<Meta> = listOf(meta).iterator()

    override fun toString(): String = "${if (meta.required) "Required" else "Optional"} ${meta.location} '${meta.name}'"

    override operator fun invoke(target: IN): FINAL = try {
        lensGet(target)
    } catch (e: LensFailure) {
        throw e
    } catch (e: Exception) {
        throw LensFailure(Invalid(meta), cause = e)
    }
}

/**
 * A BiDiLens provides the bi-directional extraction of an entity from a target, or the insertion of an entity
 * into a target.
 */
class BiDiLens<in IN, FINAL>(meta: Meta,
                             get: (IN) -> FINAL,
                             private val lensSet: (FINAL, IN) -> IN) : LensInjector<IN, FINAL>, Lens<IN, FINAL>(meta, get) {

    @Suppress("UNCHECKED_CAST")
    override operator fun <R : IN> invoke(value: FINAL, target: R): R = lensSet(value, target) as R
}
