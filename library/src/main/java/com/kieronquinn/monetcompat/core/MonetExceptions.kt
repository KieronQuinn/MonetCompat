package com.kieronquinn.monetcompat.core

class MonetPaletteException: Exception("You must include androidx.palette as a dependency to use Palette compat")
class MonetInstanceException: NullPointerException("Cannot access MonetCompat instance before calling create")
class MonetFragmentAccessException: NullPointerException("You cannot access Monet before onCreateView or after onDestroyView")
class MonetActivityAccessException: NullPointerException("You cannot access Monet before onCreate or after onDestroy")
class MonetAttributeNotFoundException(attributeName: String): Exception("Attribute $attributeName not set in your theme")
class MonetDialogException: Exception("You must call Dialog.show() before calling this method")
class MonetMaterialException: Exception("Missing Material library, include it in your dependencies or pass ALL_NO_MATERIAL to applyMonetRecursively")