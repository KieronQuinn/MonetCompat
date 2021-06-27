# MonetCompat

![MonetCompat](https://i.imgur.com/L5ku0DLl.png)

MonetCompat is an app-level implementation of kdrag0n's custom Monet implementation, used in his [android12-extensions module](https://github.com/kdrag0n/android12-extensions). It supports Android 5.0 and above (in Palette compatibility mode) and Android 8.1 and above in normal mode.

MonetCompat is currently in beta, so feedback and bug reports are greatly appreciated. It's also not currently known if Google will provide their own backwards compatibility library for Monet with Android 12's release, so this library may be replaced by an official one eventually.

No proprietary Google code is used in this project, and thus it is licensed with the MIT license for use in third party apps.

## Platform Support

As mentioned above, MonetCompat supports Android 5.0 and above. There are however a few of configurations to consider:

| Platform      | Static Wallpaper | Live Wallpaper |
| ------------- | ---------------- | -------------- |
| Android 5.0 - 8.0 (no Palette)  | ❌ | ❌ |
| Android 5.0 - 8.0 (with Palette) | ✔ | ❌ |
| Android 8.0+ | ✔ | ✔ * |

\* Live Wallpapers must implement [WallpaperService.onComputeColors](https://developer.android.com/reference/android/service/wallpaper/WallpaperService.Engine#onComputeColors()) for color extraction.

## Usage

First, follow the steps on the [Setup](https://github.com/KieronQuinn/MonetCompat/wiki/1:-Setup) page to add the dependency, and set up your Activities to handle Monet

Next, to apply Monet colors to views, check out the [Usage](https://github.com/KieronQuinn/MonetCompat/wiki/2:-Usage) page.

For more advanced usage, including explanations of other fields in MonetCompat, MonetCompatActivity and MonetFragment, see the [Advanced Usage](https://github.com/KieronQuinn/MonetCompat/wiki/3:-Advanced-Usage) page.

MonetCompat also includes a few prebuilt custom Views that implement Monet and Material You styles, as well as a custom stretch overscroll effect, like the one in Android 12. More info can be found on the [Material You](https://github.com/KieronQuinn/MonetCompat/wiki/4:-Material-You) page

## Apps using MonetCompat

[DarQ](https://github.com/KieronQuinn/DarQ)
