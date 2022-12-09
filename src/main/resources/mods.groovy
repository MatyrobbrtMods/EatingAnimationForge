ModsDotGroovy.make {
    modLoader = 'javafml'
    loaderVersion = '[44,)'

    license = 'MIT'
    issueTrackerUrl = 'https://github.com/MatyrobbrtMods/EatingAnimationForge/issues/'

    mod {
        modId = 'eatinganimation'
        displayName = 'Eating Animation'
        version = this.version

        description = 'This mod adds simple sprite animation when you eat or drink something.'
        author = 'Matyrobbrt'
        credits = 'Theoness1 for the Fabric version'

        dependencies {
            forge = "[${this.forgeVersion},)"
            minecraft = this.minecraftVersionRange
        }
    }
}