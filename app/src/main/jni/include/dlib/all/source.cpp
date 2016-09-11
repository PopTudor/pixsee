// Copyright (C) 2006  Davis E. King (davis@dlib.net)
// License: Boost Software License   See LICENSE.txt for the full license.
#ifndef DLIB_ALL_SOURCe_
#define DLIB_ALL_SOURCe_

#if defined(DLIB_ALGs_) || defined(DLIB_PLATFORm_)
#include "../dlib_basic_cpp_build_tutorial.txt"
#endif

// ISO C++ code
#include "../base64/base64_kernel_1.cpp"

// Stuff that requires C++11
#if __cplusplus >= 201103
#endif

#ifndef DLIB_ISO_CPP_ONLY
// Code that depends on OS specific APIs

// include this first so that it can disable the older version
// of the winsock API when compiled in windows.

#ifdef DLIB_PNG_SUPPORT
#include "../image_loader/png_loader.cpp"
#include "../image_saver/save_png.cpp"
#endif

#ifdef DLIB_JPEG_SUPPORT
#include "../image_loader/jpeg_loader.cpp"
#include "../image_saver/save_jpeg.cpp"
#endif

#ifndef DLIB_NO_GUI_SUPPORT

#include "../gui_widgets/fonts.cpp"
#include "../gui_widgets/widgets.cpp"
#include "../gui_widgets/drawable.cpp"
#include "../gui_widgets/canvas_drawing.cpp"
#include "../gui_widgets/style.cpp"
#include "../gui_widgets/base_widgets.cpp"
#include "../gui_core/gui_core_kernel_1.cpp"
#include "../gui_core/gui_core_kernel_2.cpp"

#endif // DLIB_NO_GUI_SUPPORT

#endif // DLIB_ISO_CPP_ONLY


#define DLIB_ALL_SOURCE_END

#endif // DLIB_ALL_SOURCe_

