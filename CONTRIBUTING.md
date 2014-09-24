#Contributions are Welcome
Just send a pull request with a description of your changes, be they bug fixes, new features, 
or design enhancement!

## Code Style
The project uses the default code style of Android Studio. Please adhere to it as best as possible.

## Method Ordering
The AS standards apply first. For all remaining:
1. Lifecycle methods, in order of occurrence (see [guide](https://github.com/xxv/android-lifecycle))
2. Methods overridden from parent class
3. Methods overridden from interfaces
4. Local methods in order of decreasing scope

## Logging
Use `Timber`, not `LOG`. For example, `Timber.d("This is a debug statement");`