#ifndef __SPHINXBASE_EXPORT_H__
#define __SPHINXBASE_EXPORT_H__

/* Win32/WinCE DLL gunk */
#if (defined(_WIN32) || defined(_WIN32_WCE)) && !defined(__SYMBIAN32__)
#if defined(SPHINXBASE_EXPORTS) /* Visual Studio */
#define SPHINXBASE_EXPORT __declspec(dllexport)
#elif defined(__CYGWIN__) /* Disable this on Cygwin, it doesn't work */
#define SPHINXBASE_EXPORT
#else
#define SPHINXBASE_EXPORT __declspec(dllimport)
#endif
#else /* !_WIN32 */
#define SPHINXBASE_EXPORT
#endif

#endif /* __SPHINXBASE_EXPORT_H__ */
