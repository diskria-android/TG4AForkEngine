package io.github.tg4afe

import org.gradle.api.NamedDomainObjectList
import org.gradle.api.model.ObjectFactory
import javax.inject.Inject

open class AppIconsListExtension @Inject constructor(objects: ObjectFactory) {

    internal val list: NamedDomainObjectList<AppIconConfig> =
        objects.namedDomainObjectList(AppIconConfig::class.java)
}
