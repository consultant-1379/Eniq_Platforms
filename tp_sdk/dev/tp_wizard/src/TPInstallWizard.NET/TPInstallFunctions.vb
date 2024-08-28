Option Strict Off
Option Explicit On 

Public NotInheritable Class TPInstallFunctions
    Dim Conxt As Designer.Context
    Dim Jn As Designer.Join
    Dim extraJoins As UnivJoins
    Dim inCompatibles As UnivIncombatibles

    ''
    ' Adds contexts to universe. Contexts are copied from source universe
    '
    ' @param Univ Specifies reference to target universe
    ' @param SrcUniv Specifies reference to source universe
    Private Sub Universe_BuildContexts(ByRef Univ As Designer.Universe, ByRef SrcUniv As Designer.Universe)

        Dim SrcJn As Designer.Join
        Dim JnFound As Boolean
        JnFound = False
        For Each Conxt In SrcUniv.Contexts
            Univ.Contexts.Add(Conxt.Name)
            For Each SrcJn In Conxt.Joins

                For Each Jn In Univ.Contexts(Conxt.Name).Joins
                    If Jn.Expression = SrcJn.Expression Then
                        JnFound = True
                        Exit For
                    End If
                Next Jn
                If JnFound = False Then
                    Jn = Univ.Contexts(Conxt.Name).Joins.Add(SrcJn.Expression)
                End If
            Next SrcJn
        Next Conxt
    End Sub

    ''
    ' Updated contexts to universe. Contexts are updated from source universe
    '
    ' @param Univ Specifies reference to target universe
    ' @param SrcUniv Specifies reference to source universe
    Private Function Universe_UpdateContexts(ByRef Univ As Designer.Universe, ByRef SrcUniv As Designer.Universe) As UnivJoins


        Dim SrcJn As Designer.Join
        Dim Jn As Designer.Join
        Dim SrcCntxt As Designer.Context
        Dim Cntxt As Designer.Context
        Dim JnFound As Boolean
        Dim CntxtFound As Boolean
        Dim First As Boolean
        JnFound = False
        Dim LogMessage As String
        Dim Count As Integer

        Dim extraJoin As UnivJoins.UnivJoin
        Dim missingJoin As UnivJoins.UnivJoin
        Dim missingJoins As UnivJoins
        Dim extraJoins As UnivJoins

        extraJoins = New UnivJoins
        'log extra joins
        For Each Cntxt In Univ.Contexts
            CntxtFound = False
            For Each SrcCntxt In SrcUniv.Contexts
                If SrcCntxt.Name = Cntxt.Name Then
                    CntxtFound = True
                    Exit For
                End If
            Next SrcCntxt
            If CntxtFound = False Then
                For Each Jn In Univ.Contexts(Cntxt.Name).Joins
                    extraJoin = New UnivJoins.UnivJoin
                    extraJoin.Expression = Jn.Expression
                    extraJoin.Contexts = Cntxt.Name
                    extraJoin.Cardinality = setCardinality(Jn)
                    extraJoins.AddItem(extraJoin)
                Next Jn
            Else
                First = True
                For Each Jn In Cntxt.Joins
                    JnFound = False
                    For Each SrcJn In SrcUniv.Contexts(Cntxt.Name).Joins
                        If UCase(SrcJn.Expression) = UCase(Jn.Expression) Then
                            JnFound = True
                            Exit For
                        End If
                    Next SrcJn
                    If JnFound = False Then
                        extraJoin = New UnivJoins.UnivJoin
                        extraJoin.Expression = Jn.Expression
                        extraJoin.Contexts = Cntxt.Name
                        extraJoin.Cardinality = setCardinality(Jn)
                        extraJoins.AddItem(extraJoin)
                    End If
                Next Jn
            End If
        Next Cntxt

        missingJoins = New UnivJoins
        'log missing joins
        For Each Cntxt In SrcUniv.Contexts
            CntxtFound = False
            For Each SrcCntxt In Univ.Contexts
                If SrcCntxt.Name = Cntxt.Name Then
                    CntxtFound = True
                    Exit For
                End If
            Next SrcCntxt
            If CntxtFound = False Then
                For Each Jn In SrcUniv.Contexts(Cntxt.Name).Joins
                    missingJoin = New UnivJoins.UnivJoin
                    missingJoin.Expression = Jn.Expression
                    missingJoin.Contexts = Cntxt.Name
                    missingJoin.Cardinality = setCardinality(Jn)
                    missingJoins.AddItem(missingJoin)
                Next Jn
            Else
                First = True
                For Each Jn In Cntxt.Joins
                    JnFound = False
                    For Each SrcJn In Univ.Contexts(Cntxt.Name).Joins
                        If UCase(SrcJn.Expression) = UCase(Jn.Expression) Then
                            JnFound = True
                            Exit For
                        End If
                    Next SrcJn
                    If JnFound = False Then
                        missingJoin = New UnivJoins.UnivJoin
                        missingJoin.Expression = Jn.Expression
                        missingJoin.Contexts = Cntxt.Name
                        missingJoin.Cardinality = setCardinality(Jn)
                        missingJoins.AddItem(missingJoin)
                    End If
                Next Jn
            End If
        Next Cntxt



        For Count = 1 To missingJoins.Count
            missingJoin = missingJoins.Item(Count)
            CntxtFound = False
            For Each Cntxt In Univ.Contexts
                If missingJoin.Contexts = Cntxt.Name Then
                    CntxtFound = True
                    Exit For
                End If
            Next Cntxt
            If CntxtFound = False Then
                Univ.Contexts.Add(missingJoin.Contexts)
            End If
        Next Count

        For Count = 1 To missingJoins.Count
            missingJoin = missingJoins.Item(Count)
            Jn = Univ.Contexts(missingJoin.Contexts).Joins.Add(missingJoin.Expression)
        Next Count

        Return extraJoins
    End Function

    Private Function Universe_GetIncompatibleObjects(ByRef Univ As Designer.Universe) As String

        Dim Tbl As Designer.Table
        Dim Obj As Designer.Object
        Dim Cond As Designer.PredefinedCondition
        Dim Jn As Designer.Join
        Dim SrcCntxt As Designer.Context
        Dim Cntxt As Designer.Context
        Dim JnFound As Boolean
        Dim CntxtFound As Boolean
        Dim First As Boolean
        JnFound = False
        Dim LogMessage As String
        Dim Count As Integer

        Dim inCompatible As UnivIncombatibles.UnivIncombatible

        inCompatibles = New UnivIncombatibles

        'log extra joins
        For Count = 1 To Univ.Tables.Count
            Tbl = Univ.Tables.Item(Count)
            For Each Obj In Tbl.IncompatibleObjects()
                inCompatible = New UnivIncombatibles.UnivIncombatible
                inCompatible.Table = Tbl.Name
                inCompatible.UnivClass = Obj.RootClass.Name
                inCompatible.UnivObject = Obj.Name
                inCompatible.Type = "object"
                inCompatibles.AddItem(inCompatible)
            Next
            For Each Cond In Tbl.IncompatiblePredefConditions()
                inCompatible = New UnivIncombatibles.UnivIncombatible
                inCompatible.Table = Tbl.Name
                inCompatible.UnivClass = Cond.RootClass.Name
                inCompatible.UnivObject = Cond.Name
                inCompatible.Type = "condition"
                inCompatibles.AddItem(inCompatible)
            Next
        Next

    End Function

    Private Function Universe_RemoveContexts(ByRef Univ As Designer.Universe, ByRef SrcUniv As Designer.Universe) As String

        Dim SrcJn As Designer.Join
        Dim Jn As Designer.Join
        Dim SrcCntxt As Designer.Context
        Dim Cntxt As Designer.Context
        Dim JnFound As Boolean
        Dim CntxtFound As Boolean
        Dim First As Boolean
        JnFound = False
        Dim LogMessage As String

        Dim extraJoin As UnivJoins.UnivJoin

        'log extra joins
        For Each Cntxt In Univ.Contexts
            CntxtFound = False
            For Each SrcCntxt In SrcUniv.Contexts
                If SrcCntxt.Name = Cntxt.Name Then
                    CntxtFound = True
                    Exit For
                End If
            Next SrcCntxt
            If CntxtFound = False Then
                For Each Jn In Univ.Contexts(Cntxt.Name).Joins
                    extraJoin = New UnivJoins.UnivJoin
                    extraJoin.Expression = Jn.Expression
                    extraJoin.Contexts = Cntxt.Name
                    extraJoin.Cardinality = setCardinality(Jn)
                    extraJoins.AddItem(extraJoin)
                Next Jn
                LogMessage &= Chr(10)
            Else
                First = True
                For Each Jn In Cntxt.Joins
                    JnFound = False
                    For Each SrcJn In SrcUniv.Contexts(Cntxt.Name).Joins
                        If UCase(SrcJn.Expression) = UCase(Jn.Expression) Then
                            JnFound = True
                            Exit For
                        End If
                    Next SrcJn
                    If JnFound = False Then
                        extraJoin = New UnivJoins.UnivJoin
                        extraJoin.Expression = Jn.Expression
                        extraJoin.Contexts = Cntxt.Name
                        extraJoin.Cardinality = setCardinality(Jn)
                        extraJoins.AddItem(extraJoin)
                    End If
                Next Jn
            End If
        Next Cntxt

        'remove duplicate contexts
        For Each Cntxt In Univ.Contexts
            Cntxt.Delete()
        Next Cntxt

        'remove duplicate joins
        For Each Jn In Univ.Joins
            Jn.Delete()
        Next Jn

        Return LogMessage

    End Function
    Private Sub Universe_RenameClasses(ByRef Univ As Designer.Universe)
        Dim Cls As Designer.Class
        For Each Cls In Univ.Classes
            If Cls.Classes.Count > 0 Then
                Universe_RenameClasses(Cls)
            End If
            Try
                Cls.Name = "C_" & Cls.Name
                If Cls.Name.Length > 35 Then
                    MsgBox("Renamed Class name " & Cls.Name & " exceeds maximum of 35 characters.")
                End If
            Catch ex As Exception
                MsgBox(ex.ToString)
            End Try

        Next Cls

    End Sub
    Private Function Universe_RenameClasses(ByRef Cls As Designer.Class)
        Dim SubCls As Designer.Class
        For Each SubCls In Cls.Classes
            If SubCls.Classes.Count > 0 Then
                Universe_RenameClasses(SubCls)
            End If
            Try
                SubCls.Name = "C_" & SubCls.Name
                If SubCls.Name.Length > 35 Then
                    MsgBox("Renamed Class name " & SubCls.Name & " exceeds maximum of 35 characters.")
                End If
            Catch ex As Exception
                MsgBox(ex.ToString)
            End Try
        Next SubCls
    End Function
    Function setCardinality(ByRef Jn As Designer.Join) As String
        If Jn.Cardinality = Designer.DsCardinality.dsManyToOneCardinality Then
            Return "n_to_1"
        End If
        If Jn.Cardinality = Designer.DsCardinality.dsOneToManyCardinality Then
            Return "1_to_n"
        End If
        If Jn.Cardinality = Designer.DsCardinality.dsOneToOneCardinality Then
            Return "1_to_1"
        End If
        If Jn.Cardinality = Designer.DsCardinality.dsManyToManyCardinality Then
            Return "n_to_n"
        End If
    End Function
    Private Sub backupUniverse(ByRef runTime As DateTime, ByRef univPath As String, ByRef univName As String, ByRef backupName As String)
        Dim backupDir As String
        Dim backupFile As String
        Dim originalFile As String

        originalFile = univPath & "\" & univName & ".unv"
        backupDir = Application.StartupPath & "\backup\" & backupName & "_" & runTime.ToString("yyyyMMdd HHmmss")
        backupFile = backupDir & "\" & univName & ".unv"
        Try
            Try
                If Not System.IO.Directory.Exists(backupDir) Then
                    System.IO.Directory.CreateDirectory(backupDir)
                End If
            Catch ex As Exception
                MsgBox("Create Directory error: " & ex.ToString)
            End Try
            If System.IO.File.Exists(originalFile) = True Then
                System.IO.File.Copy(originalFile, backupFile)
            End If
        Catch ex As Exception
            MsgBox("Copy File error: " & ex.ToString)
        End Try

    End Sub

    ''
    ' Export universe.
    '
    ' @param CMTechPack Specifies tech pack type. Value is True if tech tech pack is CM. Value is False if tech tech pack is PM.
    Function ExportUniverse(ByRef DesignerApp As Designer.Application, ByRef Domain As String, ByRef Group As String, ByRef Repository As String, ByRef SilentInstall As Boolean) As logMessages

        Dim Univ As Designer.Universe

        Dim Description As String
        Dim LongName As String
        Dim Connection As String
        Dim Name As String
        Dim Path As String
        Dim retry As Boolean
        Dim retryCount As Integer
        Dim UniverseName As String
        Dim UniverseImportName As String

        Dim try_count As Integer

        Dim logs As logMessages
        logs = New logMessages

        retry = True
        While retry = True
            Try
                retry = False
                Univ = DesignerApp.Universes.Open
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = False
                logs.AddLogText("Operation cancelled.")
                Return logs
            End Try
        End While

        'backup universe
        UniverseName = Univ.Name()
        backupUniverse(Now, Univ.Path, UniverseName, UniverseName)

        'check that is Universe already exist
        Dim universe As Designer.StoredUniverse
        For Each universe In DesignerApp.UniverseDomains(Domain).StoredUniverses
            If universe.Name = UniverseName Then
                If MsgBox("Universe " & UniverseName & " already exists in the domain. Do you want overwrite?", MsgBoxStyle.YesNo) = MsgBoxResult.No Then
                    logs.AddLogText("Operation cancelled.")
                    Return logs                    
                Else
                    Exit For
                End If
            End If
        Next

        retry = True
        try_count = 1
        While retry = True
            Try
                retry = False
                Univ.Connection = DesignerApp.Connections(1).Name
            Catch ex As Exception
                logs.AddLogText("Error retrieving connection: " & ex.Message)
                System.Threading.Thread.Sleep(2000)
                retry = True
                try_count += 1
                If try_count = 2 Then
                    Return logs
                End If
            End Try
        End While

        While retry = True
            Try
                retry = False
                Try
                    Univ.ControlOption.LimitSizeofResultSet = True
                    Univ.ControlOption.LimitExecutionTime = True
                    Univ.ControlOption.LimitSizeOfLongTextObject = False
                    Univ.ControlOption.WarnIfCostEstimateExceeded = False

                    Univ.ControlOption.LimitSizeofResultSetValue = 250000
                    Univ.ControlOption.LimitExecutionTimeValue = 120
                Catch ex As Exception
                    logs.AddLogText("Universe Control Option Exception: " & ex.Message)
                End Try

            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While


        retry = True
        try_count = 1
        While retry = True
            Try
                retry = False
                Univ.Save()
                logs.AddLogText("Saved universe '" & UniverseName & "' successfully.")
            Catch ex As Exception
                logs.AddLogText("Saving universe '" & UniverseName & "' failed. Exception: " & ex.Message)
                System.Threading.Thread.Sleep(2000)
                retry = True
                try_count += 1
                If try_count = 2 Then
                    Return logs
                End If
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                Univ.Close()
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        try_count = 1
        While retry = True
            Try
                retry = False
                DesignerApp.Universes.Export(Domain, Group, UniverseName)
                logs.AddLogText("Universe '" & UniverseName & "' exported to domain '" & Domain & "' and group '" & Group & "' successfully.")
            Catch ex As Exception
                logs.AddLogText("Universe '" & UniverseName & "' export to domain '" & Domain & "' and group '" & Group & "' failed. Exception: " & ex.Message)
                System.Threading.Thread.Sleep(2000)
                retry = True
                try_count += 1
                If try_count = 2 Then
                    Return logs
                End If
            End Try
        End While

        Return logs

    End Function

    ''
    ' Makes customer universe.
    '
    ' @param CMTechPack Specifies tech pack type. Value is True if tech tech pack is CM. Value is False if tech tech pack is PM.
    Function CreateLinkedUniverse(ByRef DesignerApp As Designer.Application, ByRef FileName As String, ByRef Domain As String, ByRef Group As String, ByRef Repository As String, ByRef SilentInstall As Boolean) As logMessages

        Dim SrcUniv As Designer.Universe
        Dim Univ As Designer.Universe

        Dim Description As String
        Dim LongName As String
        Dim Connection As String
        Dim Name As String
        Dim Path As String
        Dim retry As Boolean
        Dim retryCount As Integer
        Dim UniverseName As String
        Dim UniverseImportName As String

        Dim try_count As Integer

        Dim logs As logMessages
        logs = New logMessages

        If FileName <> "" Then
            'UniverseImportName = Replace(FileName, ".unv", "")
            UniverseImportName = FileName

            retry = True
            try_count = 1
            While retry = True
                Try
                    retry = False
                    DesignerApp.Universes.Import(Domain, UniverseImportName)
                    logs.AddLogText("Universe '" & UniverseImportName & "' imported from domain '" & Domain & "' successfully.")
                Catch ex As Exception
                    logs.AddLogText("Universe '" & UniverseImportName & "' import from domain '" & Domain & "' failed. Exception: " & ex.Message)
                    System.Threading.Thread.Sleep(2000)
                    retry = True
                    try_count += 1
                    If try_count = 2 Then
                        Return logs
                    End If
                End Try
            End While

            retry = True
            try_count = 1
            While retry = True
                Try
                    retry = False
                    If System.IO.Directory.Exists(DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\" & Repository & "\Universe\") Then
                        SrcUniv = DesignerApp.Universes.Open(DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\" & Repository & "\Universe\" & FileName & ".unv")
                    ElseIf System.IO.Directory.Exists(DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\Universe\") Then
                        SrcUniv = DesignerApp.Universes.Open(DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\Universe\" & FileName & ".unv")
                    End If
                Catch ex As Exception
                    'logs.AddLogText("Setting Source Universe '" & DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\" & Repository & "\Universe\" & FileName & ".unv" & "' failed. Exception: " & ex.Message)
                    System.Threading.Thread.Sleep(2000)
                    retry = True
                    try_count += 1
                    If try_count = 2 Then
                        Return logs
                    End If
                End Try
            End While
        Else
            retry = True
            While retry = True
                Try
                    retry = False
                    SrcUniv = DesignerApp.Universes.Open
                Catch ex As Exception
                    System.Threading.Thread.Sleep(2000)
                    retry = True
                End Try
            End While
        End If

        'backup universe
        Dim backupName As String
        backupName = SrcUniv.Name
        backupUniverse(Now, SrcUniv.Path, backupName, backupName)

        retry = True
        While retry = True
            Try
                retry = False
                Path = SrcUniv.Path()
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                LongName = Replace(SrcUniv.LongName, "TP ", "")
                Connection = SrcUniv.Connection
                Name = SrcUniv.Name
                Description = Replace(SrcUniv.Description, "TP ", "")
                Path = SrcUniv.Path()
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                Univ = DesignerApp.Universes.Add
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                Univ.Description = Description
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                Univ.LongName = LongName
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                Univ.Connection = Connection
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While
        retry = True

        While retry = True
            Try
                retry = False
                Try
                    Univ.ControlOption.LimitSizeofResultSet = True
                    Univ.ControlOption.LimitExecutionTime = True
                    Univ.ControlOption.LimitSizeOfLongTextObject = False
                    Univ.ControlOption.WarnIfCostEstimateExceeded = False

                    Univ.ControlOption.LimitSizeofResultSetValue = 250000
                    Univ.ControlOption.LimitExecutionTimeValue = 120
                Catch ex As Exception
                    logs.AddLogText("Universe Control Option Exception: " & ex.Message)
                End Try

            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While
        System.Threading.Thread.Sleep(5000)
        retry = True
        try_count = 1
        While retry = True
            Try
                retry = False
                Dim LinkedUniverse As Designer.LinkedUniverse
                LinkedUniverse = Univ.LinkedUniverses.Add(Path & "\" & Name & ".unv")
                logs.AddLogText("Linked to universe '" & Path & "\" & Name & ".unv" & "' successfully.")
            Catch ex As Exception
                logs.AddLogText("Linking to universe '" & Path & "\" & Name & ".unv" & "' failed. Exception: " & ex.Message)
                System.Threading.Thread.Sleep(2000)
                retry = True
                try_count += 1
                If try_count = 2 Then
                    Return logs
                End If
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                Path = SrcUniv.Path()
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        Call Universe_BuildContexts(Univ, SrcUniv)

        UniverseName = Replace(SrcUniv.Name, "DC", "C")

        retry = True
        try_count = 1
        While retry = True
            Try
                retry = False
                Univ.SaveAs(UniverseName)
                logs.AddLogText("Saved universe '" & UniverseName & "' successfully.")
            Catch ex As Exception
                logs.AddLogText("Saving universe '" & UniverseName & "' failed. Exception: " & ex.Message)
                System.Threading.Thread.Sleep(2000)
                retry = True
                try_count += 1
                If try_count = 2 Then
                    Return logs
                End If
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                Univ.Close()
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                SrcUniv.Close()
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        try_count = 1
        While retry = True
            Try
                retry = False
                DesignerApp.Universes.Export(Domain, Group, UniverseName)
                logs.AddLogText("Universe '" & UniverseName & "' exported to domain '" & Domain & "' and group '" & Group & "' successfully.")
            Catch ex As Exception
                logs.AddLogText("Universe '" & UniverseName & "' export to domain '" & Domain & "' and group '" & Group & "' failed. Exception: " & ex.Message)
                System.Threading.Thread.Sleep(2000)
                retry = True
                try_count += 1
                If try_count = 2 Then
                    Return logs
                End If
            End Try
        End While

        Return logs

    End Function

    ''
    ' Updates customer universe.
    '
    ' @param CMTechPack Specifies tech pack type. Value is True if tech tech pack is CM. Value is False if tech tech pack is PM.
    Function UpdateLinkedUniverse(ByRef DesignerApp As Designer.Application, ByRef FileName As String, ByRef Domain As String, ByRef Group As String, ByRef Repository As String, ByRef SilentInstall As Boolean) As logMessages

        Dim SrcUniv As Designer.Universe
        Dim Univ As Designer.Universe

        Dim Description As String
        Dim LongName As String
        Dim Connection As String
        Dim Name As String
        Dim Path As String
        Dim retry As Boolean
        Dim retryCount As Integer
        Dim UniverseName As String
        Dim UniverseImportName As String
        Dim LinkedUniverseImportName As String

        Dim try_count As Integer

        Dim logs As logMessages
        logs = New logMessages

        If FileName <> "" Then
            'UniverseImportName = Replace(FileName, ".unv", "")
            UniverseImportName = FileName

            If String.Compare(FileName.Substring(1, 1), "D") = 1 Then ' This is false.
                logs.AddLogText("Update Linked Universe not allowed, check used domain universe file (DCEXX.unv).")
                Return logs
            End If

            retry = True
            try_count = 1
            While retry = True
                Try
                    retry = False
                    DesignerApp.Universes.Import(Domain, UniverseImportName)
                    logs.AddLogText("Universe '" & UniverseImportName & "' imported from domain '" & Domain & "' successfully.")
                Catch ex As Exception
                    logs.AddLogText("Universe '" & UniverseImportName & "' import from domain '" & Domain & "' failed. Exception: " & ex.Message)
                    System.Threading.Thread.Sleep(2000)
                    retry = True
                    try_count += 1
                    If try_count = 2 Then
                        Return logs
                    End If
                End Try
            End While

            LinkedUniverseImportName = Replace(FileName, "DC", "C")

            retry = True
            try_count = 1
            While retry = True
                Try
                    retry = False
                    DesignerApp.Universes.Import(Domain, LinkedUniverseImportName)
                    logs.AddLogText("Universe '" & LinkedUniverseImportName & "' imported from domain '" & Domain & "' successfully.")
                Catch ex As Exception
                    logs.AddLogText("Universe '" & LinkedUniverseImportName & "' import from domain '" & Domain & "' failed. Exception: " & ex.Message)
                    System.Threading.Thread.Sleep(2000)
                    retry = True
                    try_count += 1
                    If try_count = 2 Then
                        Return logs
                    End If
                End Try
            End While

            retry = True
            try_count = 1
            While retry = True
                Try
                    retry = False                    

                    If System.IO.Directory.Exists(DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\" & Repository & "\Universe\") Then
                        SrcUniv = DesignerApp.Universes.Open(DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\" & Repository & "\Universe\" & FileName & ".unv")
                    ElseIf System.IO.Directory.Exists(DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\Universe\") Then
                        SrcUniv = DesignerApp.Universes.Open(DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\Universe\" & FileName & ".unv")
                    End If
                Catch ex As Exception
                    'logs.AddLogText("Setting Source Universe '" & DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\" & Repository & "\Universe\" & FileName & ".unv" & "' failed. Exception: " & ex.Message)
                    System.Threading.Thread.Sleep(2000)
                    retry = True
                    try_count += 1
                    If try_count = 2 Then
                        Return logs
                    End If
                End Try
            End While

            retry = True
            try_count = 1
            While retry = True
                Try
                    retry = False

                    If System.IO.Directory.Exists(DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\" & Repository & "\Universe\") Then                        
                        Univ = DesignerApp.Universes.Open(DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\" & Repository & "\Universe\" & LinkedUniverseImportName & ".unv")
                    ElseIf System.IO.Directory.Exists(DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\Universe\") Then                        
                        Univ = DesignerApp.Universes.Open(DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\Universe\" & LinkedUniverseImportName & ".unv")
                    End If
                Catch ex As Exception
                    'logs.AddLogText("Setting Source Universe '" & DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory) & "\" & Repository & "\Universe\" & FileName & ".unv" & "' failed. Exception: " & ex.Message)
                    System.Threading.Thread.Sleep(2000)
                    retry = True
                    try_count += 1
                    If try_count = 2 Then
                        Return logs
                    End If
                End Try
            End While
        End If

        'backup universe
        Dim runTime As DateTime
        runTime = Now
        Dim backupName As String
        Dim other_backupName As String
        backupName = SrcUniv.Name
        other_backupName = Univ.Name
        backupUniverse(runTime, SrcUniv.Path, backupName, backupName)
        backupUniverse(runTime, Univ.Path, other_backupName, backupName)

        retry = True
        While retry = True
            Try
                retry = False
                Path = SrcUniv.Path()
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                LongName = Replace(SrcUniv.LongName, "TP ", "")
                Connection = SrcUniv.Connection
                Name = SrcUniv.Name
                Description = Replace(SrcUniv.Description, "TP ", "")
                Path = SrcUniv.Path()
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                Univ.Description = Description
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                Univ.Connection = Connection
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While
        retry = True

        'TODO
        Dim extraJoins As UnivJoins
        Dim Count As Integer
        Dim Found As Boolean
        Dim First As Boolean
        Dim Cntxt As Designer.Context
        Dim extraJoin As UnivJoins.UnivJoin
        Dim ListedContexts As String
        extraJoins = Universe_UpdateContexts(Univ, SrcUniv)

        ListedContexts = ""
        First = True
        For Count = 1 To extraJoins.Count
            extraJoin = extraJoins.Item(Count)
            Found = False
            For Each Cntxt In SrcUniv.Contexts
                If extraJoin.Contexts = Cntxt.Name Then
                    Found = True
                    Exit For
                End If
            Next Cntxt
            If Found = False AndAlso InStrRev(ListedContexts, extraJoin.Contexts & ",") = 0 Then
                If First = True Then
                    logs.AddLogText(" ")
                    logs.AddLogText("Linked universe contains following extra contexts:")
                    logs.AddLogText(extraJoin.Contexts)
                    First = False
                Else
                    logs.AddLogText(extraJoin.Contexts)
                End If
                ListedContexts &= extraJoin.Contexts & ","
            End If
        Next Count

        First = True
        For Count = 1 To extraJoins.Count
            extraJoin = extraJoins.Item(Count)
            If First = True Then
                logs.AddLogText("Linked universe contains following extra joins:")
                logs.AddLogText("Join '" & extraJoin.Expression & "' in context '" & extraJoin.Contexts & "'.")
                First = False
            Else
                logs.AddLogText("Join '" & extraJoin.Expression & "' in context '" & extraJoin.Contexts & "'.")
            End If
        Next Count
        If First = False Then
            logs.AddLogText(" ")
        End If

        retry = True
        try_count = 1
        While retry = True
            Try
                retry = False
                Univ.SaveAs(LinkedUniverseImportName)
                logs.AddLogText("Saved universe '" & LinkedUniverseImportName & "' successfully.")
            Catch ex As Exception
                logs.AddLogText("Saving universe '" & LinkedUniverseImportName & "' failed. Exception: " & ex.Message)
                System.Threading.Thread.Sleep(2000)
                retry = True
                try_count += 1
                If try_count = 2 Then
                    Return logs
                End If
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                Univ.Close()
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                SrcUniv.Close()
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        try_count = 1
        While retry = True
            Try
                retry = False
                DesignerApp.Universes.Export(Domain, Group, LinkedUniverseImportName)
                logs.AddLogText("Universe '" & LinkedUniverseImportName & "' exported to domain '" & Domain & "' and group '" & Group & "' successfully.")
            Catch ex As Exception
                logs.AddLogText("Universe '" & LinkedUniverseImportName & "' export to domain '" & Domain & "' and group '" & Group & "' failed. Exception: " & ex.Message)
                System.Threading.Thread.Sleep(2000)
                retry = True
                try_count += 1
                If try_count = 2 Then
                    Return logs
                End If
            End Try
        End While

        Return logs

    End Function

    ''
    ' Makes customer universe.
    '
    ' @param CMTechPack Specifies tech pack type. Value is True if tech tech pack is CM. Value is False if tech tech pack is PM.
    Function LinkToExistingUniverse(ByRef DesignerApp As Designer.Application) As String

        Dim SrcUniv As Designer.Universe
        Dim Univ As Designer.Universe

        Dim Description As String
        Dim LongName As String
        Dim Connection As String
        Dim Name As String
        Dim Path As String
        Dim retry As Boolean
        Dim LogMessage As String

        extraJoins = New UnivJoins

        System.Threading.Thread.Sleep(2000)
        retry = True
        While retry = True
            Try
                retry = False
                DesignerApp.Visible = False
                DesignerApp.Interactive = False
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        MsgBox("First open kernel universe and after that the universe to which link is added.")

        System.Threading.Thread.Sleep(2000)
        retry = True
        While retry = True
            Try
                retry = False
                SrcUniv = DesignerApp.Universes.Open()
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        System.Threading.Thread.Sleep(2000)
        retry = True
        While retry = True
            Try
                retry = False
                Univ = DesignerApp.Universes.Open
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        System.Threading.Thread.Sleep(2000)
        Universe_RenameClasses(Univ)
        System.Threading.Thread.Sleep(2000)
        Universe_GetIncompatibleObjects(Univ)
        System.Threading.Thread.Sleep(2000)
        Universe_RemoveContexts(Univ, SrcUniv)
        System.Threading.Thread.Sleep(2000)

        retry = True
        While retry = True
            Try
                retry = False
                Dim LinkedUniverse As Designer.LinkedUniverse
                LinkedUniverse = Univ.LinkedUniverses.Add(SrcUniv.Path & "\" & SrcUniv.Name & ".unv")
            Catch ex As Exception
                MsgBox(ex.ToString)
            End Try
        End While

        System.Threading.Thread.Sleep(2000)
        retry = True
        While retry = True
            Try
                retry = False
                DesignerApp.Visible = True
                DesignerApp.Interactive = True
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        MsgBox("Verify linked universe settings." & ChrW(10) & "Click Ok, after you have done that.")

        System.Threading.Thread.Sleep(2000)
        retry = True
        While retry = True
            Try
                retry = False
                DesignerApp.Visible = False
                DesignerApp.Interactive = False
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        Universe_BuildContexts(Univ, SrcUniv)
        System.Threading.Thread.Sleep(2000)
        Universe_AddExtraJoins(Univ)
        System.Threading.Thread.Sleep(2000)
        Universe_AddExtraIncompatibles(Univ)

        System.Threading.Thread.Sleep(2000)
        retry = True
        While retry = True
            Try
                retry = False
                DesignerApp.Interactive = True
                DesignerApp.Visible = True
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        Return LogMessage

    End Function

    Private Sub Universe_AddExtraJoins(ByRef Univ As Designer.Universe)
        Dim Jn As Designer.Join
        Dim NewJn As Designer.Join
        Dim JoinCount As Integer
        Dim extraJoin As UnivJoins.UnivJoin
        Dim Cntxt As Designer.Context
        Dim JnFound As Boolean
        Dim CntxtJn As Designer.Join
        Dim Found As Boolean



        For JoinCount = 1 To extraJoins.Count
            extraJoin = extraJoins.Item(JoinCount)
            Found = False
            For Each Cntxt In Univ.Contexts
                If extraJoin.Contexts = Cntxt.Name Then
                    Found = True
                    Exit For
                End If
            Next Cntxt
            If Found = False Then
                Univ.Contexts.Add(extraJoin.Contexts)
            End If
        Next JoinCount


        For JoinCount = 1 To extraJoins.Count
            extraJoin = extraJoins.Item(JoinCount)
            Try
                Jn = Univ.Joins.Add(extraJoin.Expression)
                Jn.Cardinality = extraJoin.Cardinality
            Catch ex As Exception
                'MsgBox(ex.ToString)
                Exit Try
            End Try
        Next JoinCount

        For Each Cntxt In Univ.Contexts
            For JoinCount = 1 To extraJoins.Count
                extraJoin = extraJoins.Item(JoinCount)
                If extraJoin.Contexts = Cntxt.Name Then
                    Try
                        Jn = Cntxt.Joins.Add(extraJoin.Expression)
                    Catch ex As Exception
                        'MsgBox(ex.ToString)
                        Exit Try
                    End Try
                End If
            Next JoinCount
        Next Cntxt


    End Sub

    Private Sub Universe_AddExtraIncompatibles(ByRef Univ As Designer.Universe)
        Dim Tbl As Designer.Table
        Dim NewJn As Designer.Join
        Dim Obj As Designer.Object
        Dim Cond As Designer.PredefinedCondition
        Dim Count As Integer
        Dim TblCount As Integer
        Dim inCompatible As UnivIncombatibles.UnivIncombatible
        Dim Cntxt As Designer.Context
        Dim JnFound As Boolean
        Dim CntxtJn As Designer.Join
        Dim Found As Boolean


        For Count = 1 To inCompatibles.Count
            inCompatible = inCompatibles.Item(Count)
            If inCompatible.Type = "object" Then
                Try
                    Obj = Univ.Tables(inCompatible.Table).IncompatibleObjects.Add(inCompatible.UnivObject, inCompatible.UnivClass)
                Catch e As Exception
                    Exit Try
                End Try
            End If
            If inCompatible.Type = "condition" Then
                Try
                    Cond = Univ.Tables(inCompatible.Table).IncompatiblePredefConditions.Add(inCompatible.UnivObject, inCompatible.UnivClass)
                Catch e As Exception
                    Exit Try
                End Try
            End If
        Next Count


    End Sub

    Function GetParameter(ByRef Univ As Designer.Universe, ByRef Parameter As String) As String
        Dim retry As Boolean
        Dim Value As String
        System.Threading.Thread.Sleep(2000)
        retry = True
        While retry = True
            Try
                retry = False
                If Parameter = "Description" Then
                    Value = Replace(Univ.Description, "TP ", "")
                End If
                If Parameter = "LongName" Then
                    Value = Replace(Univ.LongName, "TP ", "")
                End If
                If Parameter = "Connection" Then
                    Value = Univ.Connection
                End If
            Catch ex As Exception
                MsgBox(ex.ToString)
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        Return Value
    End Function

End Class