package ru.vood.context.bigDto

import arrow.core.Either
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
//
//class EitherSerializer<L : Any, R : Any>(
//    private val leftSerializer: KSerializer<L>,
//    private val rightSerializer: KSerializer<R>
//) : KSerializer<Either<L, R>> {
//
//    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Either") {
//        element<L>("left")
//        element<R>("right")
//    }
//
//    override fun serialize(encoder: Encoder, value: Either<L, R>) {
//        encoder.encodeStructure(descriptor) {
//            value.fold(
//                ifLeft = { left ->
//                    encodeSerializableElement(descriptor, 0, leftSerializer, left)
//                    encodeNullElement(descriptor, 1)
//                },
//                ifRight = { right ->
//                    encodeNullElement(descriptor, 0)
//                    encodeSerializableElement(descriptor, 1, rightSerializer, right)
//                }
//            )
//        }
//    }
//
//    override fun deserialize(decoder: Decoder): Either<L, R> {
//        return decoder.decodeStructure(descriptor) {
//            var left: L? = null
//            var right: R? = null
//
//            while (true) {
//                when (val index = decodeElementIndex(descriptor)) {
//                    0 -> left = decodeSerializableElement(descriptor, 0, leftSerializer)
//                    1 -> right = decodeSerializableElement(descriptor, 1, rightSerializer)
//                    CompositeDecoder.DECODE_DONE -> break
//                    else -> error("Unexpected index: $index")
//                }
//            }
//
//            when {
//                left != null && right == null -> Either.Left(left)
//                right != null && left == null -> Either.Right(right)
//                else -> error("Either must have exactly one non-null value")
//            }
//        }
//    }
//}