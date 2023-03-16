/*
 * Copyright (c) Matyrobbrt
 * SPDX-License-Identifier: MIT
 */

ModsDotGroovy.make {
    modLoader = 'gml'
    loaderVersion = '[1,)'

    license = 'MIT'
    issueTrackerUrl = 'https://github.com/MatyrobbrtMods/EatingAnimationForge/issues/'

    mod {
        modId = 'eatinganimation'
        displayName = 'Eating Animation'
        version = this.version

        description = 'This mod adds simple sprite animation when you eat or drink something.'
        author = 'Matyrobbrt'
        credits = 'Theoness1 for the Fabric version'
        displayTest = DisplayTest.IGNORE_ALL_VERSION

        dependencies {
            forge = "[${this.forgeVersion},)"
            minecraft = this.minecraftVersionRange

            mod('gml') {
                versionRange = ">=${this.buildProperties['gml_version']}"
            }
        }
    }
}