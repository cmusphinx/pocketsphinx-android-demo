/* -*- mode: text -*- */
/* ====================================================================
 * Copyright (c) 2007 Carnegie Mellon University.  All rights
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
/* YOU MUST USE FLEX 2.5.33 OR NEWER TO PROCESS THIS FILE!!! */
%{

#include "jsgf_internal.h"
#include "jsgf_parser.h"

%}

%option 8bit reentrant bison-bridge noyywrap
%option header-file="jsgf_scanner.h"
%s COMMENT

ws       [ \t\r\n]
rulename \<[^<>]+\>
tag      \{(\\.|[^\}]+)*\}
weight   \/[0-9]*(\.[0-9]+)?\/
token    [^ \t\n;=|*+<>()\[\]{}*/]+
qstring  \"(\\.|[^"]+)*\"
bom      [\xEF][\xBB][\xBF]

%%

{ws} ;                     /* ignore whitespace */
<INITIAL>\/\/.*\n ;        /* single-line comments */
<INITIAL>\/\* { BEGIN(COMMENT); } /* C-style comments */
<COMMENT>\*\/ { BEGIN(INITIAL); }
<COMMENT>. ;               /* Ignore stuff in comment mode */

<INITIAL>{bom}?#JSGF return HEADER;
<INITIAL>grammar return GRAMMAR;
<INITIAL>import return IMPORT;
<INITIAL>public return PUBLIC;

<INITIAL>{rulename} { yylval->name = strdup(yytext); return RULENAME; }
<INITIAL>{tag}      { yylval->name = strdup(yytext); return TAG; }
<INITIAL>{token}    { yylval->name = strdup(yytext); return TOKEN; }
<INITIAL>{qstring}  { yylval->name = strdup(yytext); return TOKEN; }
<INITIAL>{weight}   { yylval->weight = atof_c(yytext+1); return WEIGHT; }

<INITIAL>. return yytext[0];        /* Single-character tokens */

%%
