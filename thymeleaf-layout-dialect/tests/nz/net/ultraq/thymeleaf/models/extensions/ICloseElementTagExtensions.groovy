package nz.net.ultraq.thymeleaf.models.extensions

import nz.net.ultraq.thymeleaf.internal.Extensions as Z;
import org.thymeleaf.model.ICloseElementTag

/**
 *
 * @author zhanhb
 */
class ICloseElementTagExtensions {
    static void apply() {
        ICloseElementTag.metaClass {
            equals << { Object other ->
                Z.equals(delegate, other)
            }
        }
    }
}
