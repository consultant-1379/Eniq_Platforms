@echo off
setlocal ENABLEDELAYEDEXPANSION

pushd %CD%\..\..\..\..
set view_path=%CD%
popd
set default_ppp_path=%view_path%/eniq_3pp

IF NOT EXIST %default_ppp_path% (
	set user_ppp_path=%view_path%\..\eniq_%USERNAME%_3pp\eniq_3pp
	IF NOT EXIST !user_ppp_path! (
		echo No 3PP directory in either %view_path% or !user_ppp_path!
		exit /b
	) ELSE (
		set ppp_path=!user_ppp_path!
	)
) ELSE (
	set ppp_path=%default_ppp_path%
)

ant -Dvobs.plat=%view_path% -Dvobs.3pp=%ppp_path% -f build_win.xml package
