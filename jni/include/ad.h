/* -*- c-basic-offset: 4; indent-tabs-mode: nil -*- */
/* ====================================================================
 * Copyright (c) 1999-2004 Carnegie Mellon University.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer. 
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * This work was supported in part by funding from the Defense Advanced 
 * Research Projects Agency and the National Science Foundation of the 
 * United States of America, and the CMU Sphinx Speech Consortium.
 *
 * THIS SOFTWARE IS PROVIDED BY CARNEGIE MELLON UNIVERSITY ``AS IS'' AND 
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT 
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY 
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 *
 */
/*
 * ad.h -- generic live audio interface for recording and playback
 * 
 * **********************************************
 * CMU ARPA Speech Project
 *
 * Copyright (c) 1996 Carnegie Mellon University.
 * ALL RIGHTS RESERVED.
 * **********************************************
 * 
 * HISTORY
 * 
 * $Log: ad.h,v $
 * Revision 1.8  2005/06/22 08:00:06  arthchan2003
 * Completed all doxygen documentation on file description for libs3decoder/libutil/libs3audio and programs.
 *
 * Revision 1.7  2004/12/14 00:39:49  arthchan2003
 * add <s3types.h> to the code, change some comments to doxygen style
 *
 * Revision 1.6  2004/12/06 11:17:55  arthchan2003
 * Update the copyright information of ad.h, *sigh* start to feel tired of updating documentation system.  Anyone who has time, please take up libs3audio. That is the last place which is undocumented
 *
 * Revision 1.5  2004/07/23 23:44:46  egouvea
 * Changed the cygwin code to use the same audio files as the MS Visual code, removed unused variables from fe_interface.c
 *
 * Revision 1.4  2004/02/29 23:48:31  egouvea
 * Updated configure.in to the recent automake/autoconf, fixed win32
 * references in audio files.
 *
 * Revision 1.3  2002/11/10 19:27:38  egouvea
 * Fixed references to sun's implementation of audio interface,
 * referring to the correct .h file, and replacing sun4 with sunos.
 *
 * Revision 1.2  2001/12/11 04:40:55  lenzo
 * License cleanup.
 *
 * Revision 1.1.1.1  2001/12/03 16:01:45  egouvea
 * Initial import of sphinx3
 *
 * Revision 1.1.1.1  2001/01/17 05:17:14  ricky
 * Initial Import of the s3.3 decoder, has working decodeaudiofile, s3.3_live
 *
 * 
 * 19-Jan-1999	M K Ravishankar (rkm@cs.cmu.edu) at Carnegie Mellon University
 * 		Added AD_ return codes.  Added ad_open_sps_bufsize(), and
 * 		ad_rec_t.n_buf.
 * 
 * 17-Apr-98	M K Ravishankar (rkm@cs.cmu.edu) at Carnegie Mellon University
 * 		Added ad_open_play_sps().
 * 
 * 07-Mar-98	M K Ravishankar (rkm@cs.cmu.edu) at Carnegie Mellon University
 * 		Added ad_open_sps().
 * 
 * 10-Jun-96	M K Ravishankar (rkm@cs.cmu.edu) at Carnegie Mellon University
 * 		Added ad_wbuf_t, ad_rec_t, and ad_play_t types, and augmented all
 * 		recording functions with ad_rec_t, and playback functions with
 * 		ad_play_t.
 * 
 * 06-Jun-96	M K Ravishankar (rkm@cs.cmu.edu) at Carnegie Mellon University
 *		Created.
 */

/** \file ad.h
 * \brief generic live audio interface for recording and playback
 */

#ifndef _AD_H_
#define _AD_H_

#include <sphinx_config.h>

#if defined (__CYGWIN__)

#include <w32api/windows.h>
#include <w32api/mmsystem.h>

#elif (defined(WIN32) && !defined(GNUWINCE)) || defined(_WIN32_WCE)

#include <windows.h>
#include <mmsystem.h>

#elif defined(AD_BACKEND_OSF) /* Not implemented, it seems */

#include <AF/AFlib.h>

#elif defined(AD_BACKEND_HPUX) /* Not implemented, it seems */

#include <audio/Alib.h>

#elif defined(AD_BACKEND_ALSA)

#include <alsa/asoundlib.h>

#elif defined(AD_BACKEND_IRIX)

#include <dmedia/audio.h>

#elif defined(AD_BACKEND_PORTAUDIO)

#include "portaudio.h"
#include "pablio.h"

#endif

/* Win32/WinCE DLL gunk */
#include <sphinxbase_export.h>

#include <prim_type.h>


#ifdef __cplusplus
extern "C" {
#endif
#if 0
/* Fool Emacs. */
}
#endif

#define AD_SAMPLE_SIZE		(sizeof(int16))
#define DEFAULT_SAMPLES_PER_SEC	16000

/* Return codes */
#define AD_OK		0
#define AD_EOF		-1
#define AD_ERR_GEN	-1
#define AD_ERR_NOT_OPEN	-2
#define AD_ERR_WAVE	-3


#if  (defined(WIN32) || defined(AD_BACKEND_WIN32)) && !defined(GNUWINCE)
typedef struct {
    HGLOBAL h_whdr;
    LPWAVEHDR p_whdr;
    HGLOBAL h_buf;
    LPSTR p_buf;
} ad_wbuf_t;
#endif


/* ------------ RECORDING -------------- */

/*
 * NOTE: ad_rec_t and ad_play_t are READ-ONLY structures for the user.
 */

#if (defined(WIN32) || defined(AD_BACKEND_WIN32)) && !defined(GNUWINCE)

#define DEFAULT_DEVICE (char*)DEV_MAPPER

/**
 * Audio recording structure. 
 */
typedef struct ad_rec_s {
    HWAVEIN h_wavein;	/* "HANDLE" to the audio input device */
    ad_wbuf_t *wi_buf;	/* Recording buffers provided to system */
    int32 n_buf;	/* #Recording buffers provided to system */
    int32 opened;	/* Flag; A/D opened for recording */
    int32 recording;
    int32 curbuf;	/* Current buffer with data for application */
    int32 curoff;	/* Start of data for application in curbuf */
    int32 curlen;	/* #samples of data from curoff in curbuf */
    int32 lastbuf;	/* Last buffer containing data after recording stopped */
    int32 sps;		/* Samples/sec */
    int32 bps;		/* Bytes/sample */
} ad_rec_t;

#elif defined(AD_BACKEND_OSF)

#define DEFAULT_DEVICE NULL

typedef struct {
    AFAudioConn *aud;
    AC ac;
    int32 recording;		/* flag; TRUE iff currently recording */
    ATime last_rec_time;	/* timestamp of last sample recorded in buffer */
    ATime end_rec_time;		/* time at which recording stopped */
    int32 sps;		/* Samples/sec */
    int32 bps;		/* Bytes/sample */
} ad_rec_t;

#elif defined(AD_BACKEND_SUNOS)

#define DEFAULT_DEVICE "/dev/audio"

typedef struct {
    int32 audio_fd;
    int32 recording;
    int32 sps;		/* Samples/sec */
    int32 bps;		/* Bytes/sample */
} ad_rec_t;

#elif defined(AD_BACKEND_OSS) || defined(AD_BACKEND_OSS_BSD)

#define DEFAULT_DEVICE "/dev/dsp"

/** \struct ad_rec_t
 *  \brief Audio recording structure. 
 */

/* Added by jd5q+@andrew.cmu.edu, 10/3/1997: */
typedef struct {
    int32 dspFD;	/* Audio device descriptor */
    int32 recording;
    int32 sps;		/* Samples/sec */
    int32 bps;		/* Bytes/sample */
} ad_rec_t;

#elif defined(AD_BACKEND_ESD)

#define DEFAULT_DEVICE NULL
typedef struct {
    int32 fd;
    int32 recording;
    int32 sps;
    int32 bps;
} ad_rec_t;

#elif defined(AD_BACKEND_ALSA)

#define DEFAULT_DEVICE "default"
typedef struct {
    snd_pcm_t *dspH;
    int32 recording;
    int32 sps;
    int32 bps;
} ad_rec_t;

#elif defined(AD_BACKEND_HPUX)

#define DEFAULT_DEVICE NULL
typedef struct {
    Audio *audio;	/* The main audio handle */
    ATransID xid;	/* The current transaction ID */
    int32 streamSocket;	/* Connection socket */
    int32 recording;	/* TRUE iff currently recording */
    int32 sps;		/* Samples/sec */
    int32 bps;		/* Bytes/sample */
} ad_rec_t;

#elif defined(AD_BACKEND_IRIX)
#define DEFAULT_DEVICE NULL
typedef struct {
    ALport audio;	/* The main audio handle */
    int32 recording;	/* TRUE iff currently recording */
    int32 sps;		/* Samples/sec */
    int32 bps;		/* Bytes/sample */
} ad_rec_t;

#elif defined(AD_BACKEND_PORTAUDIO)
#define DEFAULT_DEVICE NULL /* FIXME */
typedef struct {
  PABLIO_Stream *astream;
  int32 sps;
  int32 bps;
  int32 recording;
} ad_rec_t;

#elif defined(AD_BACKEND_S60)

typedef struct ad_rec_s {
    void* recorder;
    int32 recording;
    int32 sps;
    int32 bps;
} ad_rec_t;

SPHINXBASE_EXPORT
ad_rec_t *ad_open_sps_bufsize (int32 samples_per_sec, int32 bufsize_msec);

#else

#define DEFAULT_DEVICE NULL
typedef struct {
    int32 sps;		/**< Samples/sec */
    int32 bps;		/**< Bytes/sample */
} ad_rec_t;	


#endif


/**
 * Open a specific audio device for recording.
 *
 * The device is opened in non-blocking mode and placed in idle state.
 *
 * @return pointer to read-only ad_rec_t structure if successful, NULL
 * otherwise.  The return value to be used as the first argument to
 * other recording functions.
 */
SPHINXBASE_EXPORT
ad_rec_t *ad_open_dev (
	const char *dev, /**< Device name (platform-specific) */
	int32 samples_per_sec /**< Samples per second */
	);

/**
 * Open the default audio device with a given sampling rate.
 */
SPHINXBASE_EXPORT
ad_rec_t *ad_open_sps (
		       int32 samples_per_sec /**< Samples per second */
		       );


/**
 * Open the default audio device.
 */
SPHINXBASE_EXPORT
ad_rec_t *ad_open ( void );


#if defined(WIN32) && !defined(GNUWINCE)
/*
 * Like ad_open_sps but specifies buffering required within driver.  This function is
 * useful if the default (5000 msec worth) is too small and results in loss of data.
 */
SPHINXBASE_EXPORT
ad_rec_t *ad_open_sps_bufsize (int32 samples_per_sec, int32 bufsize_msec);
#endif


/* Start audio recording.  Return value: 0 if successful, <0 otherwise */
SPHINXBASE_EXPORT
int32 ad_start_rec (ad_rec_t *);


/* Stop audio recording.  Return value: 0 if successful, <0 otherwise */
SPHINXBASE_EXPORT
int32 ad_stop_rec (ad_rec_t *);


/* Close the recording device.  Return value: 0 if successful, <0 otherwise */
SPHINXBASE_EXPORT
int32 ad_close (ad_rec_t *);


/*
 * Read next block of audio samples while recording; read upto max samples into buf.
 * Return value: # samples actually read (could be 0 since non-blocking); -1 if not
 * recording and no more samples remaining to be read from most recent recording.
 */
SPHINXBASE_EXPORT
int32 ad_read (ad_rec_t *, int16 *buf, int32 max);


/* ------ PLAYBACK; SIMILAR TO RECORDING ------- */

#if defined(WIN32) && !defined(GNUWINCE)

typedef struct {
    HWAVEOUT h_waveout;	/* "HANDLE" to the audio output device */
    ad_wbuf_t *wo_buf;	/* Playback buffers given to the system */
    int32 opened;	/* Flag; A/D opened for playback */
    int32 playing;
    char *busy;		/* flags [N_WO_BUF] indicating whether given to system */
    int32 nxtbuf;	/* Next buffer [0..N_WO_BUF-1] to be used for playback data */
    int32 sps;		/* Samples/sec */
    int32 bps;		/* Bytes/sample */
} ad_play_t;

#else

typedef struct {
    int32 sps;		/* Samples/sec */
    int32 bps;		/* Bytes/sample */
} ad_play_t;	/* Dummy definition for systems without A/D stuff */

#endif


SPHINXBASE_EXPORT
ad_play_t *ad_open_play_sps (int32 samples_per_sec);

SPHINXBASE_EXPORT
ad_play_t *ad_open_play ( void );

SPHINXBASE_EXPORT
int32 ad_start_play (ad_play_t *);

SPHINXBASE_EXPORT
int32 ad_stop_play (ad_play_t *);

SPHINXBASE_EXPORT
int32 ad_close_play (ad_play_t *);


/**
 * Queue a block of audio samples for playback.
 *
 * Write the next block of [len] samples from rawbuf to the A/D device for playback.
 * The device may queue less than len samples, possibly 0, since it is non-blocking.
 * The application should resubmit the remaining data to be played.
 * Return value: # samples accepted for playback; -1 if error.
 */
SPHINXBASE_EXPORT
int32 ad_write (ad_play_t *, int16 *buf, int32 len);


/* ------ MISCELLANEOUS ------- */

/**
 * Convert mu-law data to int16 linear PCM format.
 */
SPHINXBASE_EXPORT
void ad_mu2li (int16 *outbuf,		/* Out: PCM data placed here (allocated by user) */
	       unsigned char *inbuf,	/* In: Input buffer with mulaw data */
	       int32 n_samp);		/* In: #Samples in inbuf */

#ifdef __cplusplus
}
#endif


#endif
