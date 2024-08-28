Option Strict Off
Option Explicit On

Imports System.Net

Public NotInheritable Class TPInstallFunctionsTPIDE
    Dim Conxt As Designer.Context
    Dim Jn As Designer.Join
    Dim extraJoins As UnivJoinsTPIde
    Dim inCompatibles As UnivIncombatiblesTPIDE

    ''
    ' Adds contexts to universe. Contexts are copied from source universe
    '
    ' @param Univ Specifies reference to target universe
    ' @param SrcUniv Specifies reference to source universe
    Private Sub Universe_BuildContexts(ByRef Univ As Designer.Universe, ByRef SrcUniv As Designer.Universe)
        Trace.WriteLine("Adding contexts and joins to linked universe")
        Dim SrcJn As Designer.Join
        Dim JnFound As Boolean
        JnFound = False
        For Each Conxt In SrcUniv.Contexts
            Univ.Contexts.Add(Conxt.Name)
            Trace.WriteLine("Added context " & Conxt.Name & " from source universe to linked universe " & Univ.LongName)
            For Each SrcJn In Conxt.Joins
                Trace.WriteLine("Checking source join " & SrcJn.Expression)

                For Each Jn In Univ.Contexts(Conxt.Name).Joins
                    If Jn.Expression = SrcJn.Expression Then
                        JnFound = True
                        Trace.WriteLine("Found source join " & SrcJn.Expression)
                        Exit For
                    End If
                Next Jn
                If JnFound = False Then
                    Jn = Univ.Contexts(Conxt.Name).Joins.Add(SrcJn.Expression)
                    Trace.WriteLine("Added join " & SrcJn.Expression & " from " & Conxt.Name & " context in source universe to linked universe " & Univ.LongName)
                End If
            Next SrcJn
        Next Conxt
    End Sub
    Private Function Universe_UpdateContexts(ByRef Univ As Designer.Universe, ByRef SrcUniv As Designer.Universe) As UnivJoinsTPIde


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

        Dim extraJoin As UnivJoinsTPIde.UnivJoin
        Dim missingJoin As UnivJoinsTPIde.UnivJoin
        Dim missingJoins As UnivJoinsTPIde
        Dim extraJoins As UnivJoinsTPIde

        Try
            extraJoins = New UnivJoinsTPIde
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
                    Try
                        For Each Jn In Univ.Contexts(Cntxt.Name).Joins
                            extraJoin = New UnivJoinsTPIde.UnivJoin
                            extraJoin.Expression = Jn.Expression
                            extraJoin.Contexts = Cntxt.Name
                            extraJoin.Cardinality = setCardinality(Jn)
                            extraJoins.AddItem(extraJoin)
                            Console.WriteLine("Adding the Cntxt Joins which are missed")
                        Next Jn
                    Catch ex As Exception
                        'Console.WriteLine("Join exception: " & ex.ToString)
                    End Try
                Else
                    First = True
                    Try
                        For Each Jn In Cntxt.Joins
                            JnFound = False
                            Try
                                For Each SrcJn In SrcUniv.Contexts(Cntxt.Name).Joins
                                    If UCase(SrcJn.Expression) = UCase(Jn.Expression) Then
                                        JnFound = True
                                        Exit For
                                    End If
                                Next SrcJn
                            Catch e As Exception
                                'Console.WriteLine("Join exception: " & e.ToString)
                            End Try
                            If JnFound = False Then
                                extraJoin = New UnivJoinsTPIde.UnivJoin
                                extraJoin.Expression = Jn.Expression
                                extraJoin.Contexts = Cntxt.Name
                                extraJoin.Cardinality = setCardinality(Jn)
                                extraJoins.AddItem(extraJoin)
                                Console.WriteLine("Adding the JnFound Joins which are missed ")
                            End If
                        Next Jn
                    Catch ex As Exception
                        'Console.WriteLine("Join exception: " & ex.ToString)
                    End Try
                End If
            Next Cntxt

            missingJoins = New UnivJoinsTPIde
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
                    Try
                        For Each Jn In SrcUniv.Contexts(Cntxt.Name).Joins
                            missingJoin = New UnivJoinsTPIde.UnivJoin
                            missingJoin.Expression = Jn.Expression
                            missingJoin.Contexts = Cntxt.Name
                            missingJoin.Cardinality = setCardinality(Jn)
                            missingJoins.AddItem(missingJoin)
                        Next Jn
                    Catch ex As Exception
                        'Console.WriteLine("Join exception: " & ex.ToString)
                    End Try
                Else
                    First = True
                    Try
                        For Each Jn In Cntxt.Joins
                            JnFound = False
                            Try
                                For Each SrcJn In Univ.Contexts(Cntxt.Name).Joins
                                    If UCase(SrcJn.Expression) = UCase(Jn.Expression) Then
                                        JnFound = True
                                        Exit For
                                    End If
                                Next SrcJn
                            Catch e As Exception
                                'Console.WriteLine("Join exception: " & e.ToString)
                            End Try
                            If JnFound = False Then
                                missingJoin = New UnivJoinsTPIde.UnivJoin
                                missingJoin.Expression = Jn.Expression
                                missingJoin.Contexts = Cntxt.Name
                                missingJoin.Cardinality = setCardinality(Jn)
                                missingJoins.AddItem(missingJoin)
                            End If
                        Next Jn
                    Catch ex As Exception
                        'Console.WriteLine("Join exception: " & ex.ToString)
                    End Try
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
                Try
                    missingJoin = missingJoins.Item(Count)
                    Jn = Univ.Contexts(missingJoin.Contexts).Joins.Add(missingJoin.Expression)
                Catch ex As Exception
                    Console.WriteLine("Error adding join: " & missingJoin.Expression & " to " & missingJoin.Contexts)
                End Try
            Next Count
        Catch ex As Exception
            Console.WriteLine("Exception on updating joins and contexts:" & ex.ToString)
            Return Nothing
        End Try

        Return extraJoins
    End Function

    ''
    ' Updated contexts to universe. Contexts are updated from source universe
    '
    ' @param Univ Specifies reference to target universe
    ' @param SrcUniv Specifies reference to source universe
    Private Function Universe_UpdateContexts_new(ByRef Univ As Designer.Universe, ByRef SrcUniv As Designer.Universe) As UnivJoinsTPIde


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
        Dim ItemCount As Integer
        Dim SubItemCount As Integer

        Dim extraJoin As UnivJoinsTPIde.UnivJoin
        Dim missingJoin As UnivJoinsTPIde.UnivJoin
        Dim missingJoins As UnivJoinsTPIde
        Dim extraJoins As UnivJoinsTPIde

        extraJoins = New UnivJoinsTPIde
        'log extra joins

        Try
            For Count = 1 To Univ.Contexts.Count
                Cntxt = Univ.Contexts.Item(Count)
                CntxtFound = False
                For ItemCount = 1 To SrcUniv.Contexts.Count
                    SrcCntxt = SrcUniv.Contexts.Item(ItemCount)
                    If SrcCntxt.Name = Cntxt.Name Then
                        CntxtFound = True
                        Exit For
                    End If
                Next
                If CntxtFound = False Then
                    For ItemCount = 1 To Univ.Contexts(Cntxt.Name).Joins.Count
                        Jn = Univ.Contexts(Cntxt.Name).Joins.Item(ItemCount)
                        extraJoin = New UnivJoinsTPIde.UnivJoin
                        extraJoin.Expression = Jn.Expression
                        extraJoin.Contexts = Cntxt.Name
                        extraJoin.Cardinality = setCardinality(Jn)
                        extraJoins.AddItem(extraJoin)
                    Next
                Else
                    First = True

                    For ItemCount = 1 To Cntxt.Joins.Count
                        Jn = Cntxt.Joins.Item(ItemCount)
                        JnFound = False
                        For SubItemCount = 1 To SrcUniv.Context(Cntxt.Name).Joins.Count
                            SrcJn = SrcUniv.Contexts(Cntxt.Name).Joins.Item(SubItemCount)
                            If UCase(SrcJn.Expression) = UCase(Jn.Expression) Then
                                JnFound = True
                                Exit For
                            End If
                        Next
                        If JnFound = False Then
                            extraJoin = New UnivJoinsTPIde.UnivJoin
                            extraJoin.Expression = Jn.Expression
                            extraJoin.Contexts = Cntxt.Name
                            extraJoin.Cardinality = setCardinality(Jn)
                            extraJoins.AddItem(extraJoin)
                        End If
                    Next
                End If
            Next
        Catch ex As Exception
            Console.WriteLine("Exception on getting extra joins:" & ex.ToString)
            Return Nothing
        End Try

        missingJoins = New UnivJoinsTPIde
        'log missing joins

        Try
            For Count = 1 To SrcUniv.Contexts.Count
                Cntxt = SrcUniv.Contexts.Item(Count)
                CntxtFound = False
                For ItemCount = 1 To Univ.Contexts.Count
                    SrcCntxt = Univ.Contexts.Item(ItemCount)
                    If SrcCntxt.Name = Cntxt.Name Then
                        CntxtFound = True
                        Exit For
                    End If
                Next
                If CntxtFound = False Then
                    For ItemCount = 1 To SrcUniv.Contexts(Cntxt.Name).Joins.Count
                        Jn = SrcUniv.Contexts(Cntxt.Name).Joins.Item(ItemCount)
                        missingJoin = New UnivJoinsTPIde.UnivJoin
                        missingJoin.Expression = Jn.Expression
                        missingJoin.Contexts = Cntxt.Name
                        missingJoin.Cardinality = setCardinality(Jn)
                        missingJoins.AddItem(missingJoin)
                    Next
                Else
                    First = True
                    For ItemCount = 1 To Cntxt.Joins.Count
                        Jn = Cntxt.Joins.Item(ItemCount)
                        JnFound = False
                        For SubItemCount = 1 To Univ.Contexts(Cntxt.Name).Joins.Count
                            SrcJn = Univ.Contexts(Cntxt.Name).Joins.Item(SubItemCount)
                            If UCase(SrcJn.Expression) = UCase(Jn.Expression) Then
                                JnFound = True
                                Exit For
                            End If
                        Next
                        If JnFound = False Then
                            missingJoin = New UnivJoinsTPIde.UnivJoin
                            missingJoin.Expression = Jn.Expression
                            missingJoin.Contexts = Cntxt.Name
                            missingJoin.Cardinality = setCardinality(Jn)
                            missingJoins.AddItem(missingJoin)
                        End If
                    Next
                End If
            Next
        Catch ex As Exception
            Console.WriteLine("Exception on getting missing joins:" & ex.ToString)
            Return Nothing
        End Try

        Try
            For Count = 1 To missingJoins.Count
                missingJoin = missingJoins.Item(Count)
                CntxtFound = False
                For ItemCount = 1 To Univ.Contexts.Count
                    Cntxt = Univ.Contexts.Item(ItemCount)
                    If missingJoin.Contexts = Cntxt.Name Then
                        CntxtFound = True
                        Exit For
                    End If
                Next
                If CntxtFound = False Then
                    Univ.Contexts.Add(missingJoin.Contexts)
                End If
            Next Count

            For Count = 1 To missingJoins.Count
                missingJoin = missingJoins.Item(Count)
                Jn = Univ.Contexts(missingJoin.Contexts).Joins.Add(missingJoin.Expression)
            Next Count
        Catch ex As Exception
            Console.WriteLine("Exception on adding missing joins:" & ex.ToString)
            Return Nothing
        End Try

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

        Dim inCompatible As UnivIncombatiblesTPIDE.UnivIncombatible

        inCompatibles = New UnivIncombatiblesTPIDE

        'log extra joins
        For Count = 1 To Univ.Tables.Count
            Tbl = Univ.Tables.Item(Count)
            For Each Obj In Tbl.IncompatibleObjects()
                inCompatible = New UnivIncombatiblesTPIDE.UnivIncombatible
                inCompatible.Table = Tbl.Name
                inCompatible.UnivClass = Obj.RootClass.Name
                inCompatible.UnivObject = Obj.Name
                inCompatible.Type = "object"
                inCompatibles.AddItem(inCompatible)
            Next
            For Each Cond In Tbl.IncompatiblePredefConditions()
                inCompatible = New UnivIncombatiblesTPIDE.UnivIncombatible
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

        Dim extraJoin As UnivJoinsTPIde.UnivJoin

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
                    extraJoin = New UnivJoinsTPIde.UnivJoin
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
                        extraJoin = New UnivJoinsTPIde.UnivJoin
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
                If Cls.Name.Length > 128 Then
                    Console.WriteLine("Renamed Class name " & Cls.Name & " exceeds maximum of 128 characters.")
                End If
            Catch ex As Exception
                Console.WriteLine(ex.ToString)
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
                If SubCls.Name.Length > 128 Then
                    Console.WriteLine("Renamed Class name " & SubCls.Name & " exceeds maximum of 128 characters.")
                End If
            Catch ex As Exception
                Console.WriteLine(ex.ToString)
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
                    Console.WriteLine("Created backup directory for universe: " & backupDir)
                End If
            Catch ex As Exception
                Console.WriteLine("Create Directory Exception: " & ex.ToString)
            End Try
            If System.IO.File.Exists(originalFile) = True Then
                System.IO.File.Copy(originalFile, backupFile)
                Console.WriteLine("Created universe backup from : " & originalFile & " to " & backupFile)
            End If
        Catch ex As Exception
            Console.WriteLine("Copy File Exception: " & ex.ToString)
        End Try

    End Sub

    ''
    ' Export universe.
    '
    Function ExportUniverse(ByRef DesignerApp As Designer.Application, ByRef Domain As String, ByRef Group As String, ByRef Repository As String, ByRef SilentInstall As Boolean) As Boolean

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

        retry = True
        While retry = True
            Try
                retry = False
                Univ = DesignerApp.Universes.Open
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = False
                Trace.WriteLine("Operation cancelled.")
                Return False
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
                    Trace.WriteLine("Operation cancelled.")
                    Return False
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
                Trace.WriteLine("Error retrieving connection: " & ex.Message)
                System.Threading.Thread.Sleep(2000)
                retry = True
                try_count += 1
                If try_count = 2 Then
                    Return False
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
                    Trace.WriteLine("Universe Control Option Exception: " & ex.Message)
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
                Trace.WriteLine("Saved universe '" & UniverseName & "' successfully.")
            Catch ex As Exception
                Trace.WriteLine("Saving universe '" & UniverseName & "' failed. Exception: " & ex.Message)
                System.Threading.Thread.Sleep(2000)
                retry = True
                try_count += 1
                If try_count = 2 Then
                    Return False
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
                Trace.WriteLine("Universe '" & UniverseName & "' exported to domain '" & Domain & "' and group '" & Group & "' successfully.")
            Catch ex As Exception
                Trace.WriteLine("Universe '" & UniverseName & "' export to domain '" & Domain & "' and group '" & Group & "' failed. Exception: " & ex.Message)
                System.Threading.Thread.Sleep(2000)
                retry = True
                try_count += 1
                If try_count = 2 Then
                    Return False
                End If
            End Try
        End While

        Return True

    End Function

    ''
    'Opens a universe. 
    'Called after the universe has been imported from the BO server to a local directory.
    '@param boRep The BO server name.
    '@param FileName The filename of the universe.
    '@param Domain The domain is the directory the universe is in on the BO server.
    '@param DesignerApp An instance of the business objects DesignerApp.
    '@returns The source universe (a Designer.Universe object)
    Protected Function openUniverse(ByVal boRep As String, ByVal FileName As String, ByVal Domain As String, _
                                    ByRef DesignerApp As Designer.Application) As Designer.Universe
        Dim universe As Designer.Universe

        'XI CMS Server directory path:        
        'dcweb4-a:6400 --> @dcweb4-a_6400
        Dim serverDirectory As String
        serverDirectory = Replace(boRep, ":", "_")
        serverDirectory = "@" + serverDirectory

        Try
            ' Try to open the universe using the server directory and domain as the directory:
            Dim directory As String = "\" & serverDirectory & "\" & Domain & "\"
            universe = openUniverseFromServerName(DesignerApp, directory, FileName)

            ' Try using only the domain as the directory:
            If universe Is Nothing Then
                directory = "\" & Domain & "\"
                universe = openUniverseFromServerName(DesignerApp, directory, FileName)
            End If

            ' Check if the full server address has been given:
            If universe Is Nothing Then
                directory = getDirectoryNameFromFullAddress(serverDirectory)
                directory = "\" & directory & "\" & Domain & "\"
                universe = openUniverseFromServerName(DesignerApp, directory, FileName)
            End If

            ' Try to open universe using IP address:
            If universe Is Nothing Then
                Console.WriteLine("Checking if repository server is specified as an IP address: " & boRep)
                directory = getDirectoryNameFromIPAddress(boRep)
                If Not (directory Is Nothing) AndAlso (directory <> "") Then
                    directory = "\" & directory & "\" & Domain & "\"
                    universe = openUniverseFromServerName(DesignerApp, directory, FileName)
                Else
                    Console.WriteLine("Repository is not an IP address")
                End If
            End If
        Catch ex As Exception
            Trace.WriteLine("Exception while opening universe: " & ex.ToString())
        End Try

        ' Couldn't open the universe:
        If (universe Is Nothing) Then
            Console.WriteLine("Error: failed to open universe")
            Throw New Exception("Error: failed to open universe")
        End If
        Return universe
    End Function

    ''
    'Opens a universe when the BO server name is given just with the host name e.g. atrcx886vm3 (not the full host name)
    '@param DesignerApp
    '@param subDirectory 
    '@param FileName
    '@returns A universe object is returned if it opens ok, otherwise the return value is Nothing.
    Protected Function openUniverseFromServerName(ByRef DesignerApp As Designer.Application, ByVal subDirectory As String, _
                                                  ByVal FileName As String) As Designer.Universe
        Dim universe As Designer.Universe
        universe = Nothing

        ' Get the local directory where universes are stored:
        Dim universeDirectory As String = DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory)
        Trace.WriteLine("Got local BO directory: " & universeDirectory)

        ' Open the universe:
        If System.IO.Directory.Exists(universeDirectory & subDirectory) Then
            Console.WriteLine("Opening " & universeDirectory & subDirectory & FileName & ".unv")
            universe = DesignerApp.Universes.Open(universeDirectory & subDirectory & FileName & ".unv")
        End If
        Return universe
    End Function

    ''
    'Takes a BO server name as an IP address and returns the directory name.
    '@param boRep BO repository address in the format: 100.100.100.100_1234.
    '@returns The directory name e.g. @atrcx886vm3_6400
    Protected Function getDirectoryNameFromIPAddress(ByVal boRep As String) As String
        Dim directoryName As String
        directoryName = ""

        Try
            'Replace : with _ in address:
            Dim boRepWithUnderscore As String
            boRepWithUnderscore = Replace(boRep, ":", "_")
            ' Split the string into two parts:
            Dim ipAddressTokens As String()
            ipAddressTokens = Split(boRepWithUnderscore, "_")
            ' Check the string is split correctly:
            If (ipAddressTokens.Length <> 2) Then
                Throw New Exception("IP address did not parse correctly. IP address must be in the format: <127.0.0.0:6400>")
            End If
            ' Get the IPAddress object:
            Dim ipString = ipAddressTokens(0)
            Dim ipAdd As System.Net.IPAddress
            ipAdd = getIPAddress(ipString)
            ' Get the port number:
            Dim port As String
            port = ipAddressTokens(1)

            If Not (ipAdd Is Nothing) AndAlso Not (port Is Nothing) AndAlso (port <> "") Then
                Dim remoteHostEntry As IPHostEntry
                remoteHostEntry = Dns.GetHostByAddress(ipAdd)
                directoryName = remoteHostEntry.HostName()
                directoryName = getDirectoryNameFromFullAddress("@" + directoryName + "_" + port)
            End If
        Catch ex As Exception
            Trace.WriteLine("Error getting directory name from IP address (may not be an IP address): " & ex.ToString())
        End Try
        Return directoryName
    End Function

    ''
    'Takes a full server name and returns the local BO repository directory name.
    '(e.g. @atrcx886vm3.athtem.eei.ericsson.se_6400 -> @atrcx886vm3_6400)
    '@param boRep The BO repository server in the format: @atrcx886vm3.athtem.eei.ericsson.se_6400.
    '@returns The directory name e.g. @atrcx886vm3_6400
    Protected Function getDirectoryNameFromFullAddress(ByVal boRep As String) As String
        Dim directoryName As String
        directoryName = ""
        Try
            ' Get server name:
            Dim serverNameTokens As String()
            serverNameTokens = Split(boRep, ".")
            directoryName = serverNameTokens(0)
            ' Get server port:
            Dim serverPortTokens As String()
            serverPortTokens = Split(boRep, "_")
            Dim serverPort = serverPortTokens(1)
            directoryName = directoryName + "_" + serverPort
        Catch ex As Exception
            Trace.WriteLine("Error getting server name: " & ex.ToString())
        End Try
        Return directoryName
    End Function

    ''
    'Gets the IPAddress object for an IP address.
    '@param ipString Server address as an IP string.
    '@returns IPAddress if address is a valid IP address.
    Private Function getIPAddress(ByVal ipString As String) As System.Net.IPAddress
        Dim ipAdd As IPAddress
        Try
            ipAdd = IPAddress.Parse(ipString)
        Catch ex As System.ArgumentNullException
            Trace.WriteLine("IP string is null: " & ex.ToString())
        Catch formatException As System.FormatException
            Trace.WriteLine("IP string is not a valid IP address")
        Catch otherException As Exception
            Trace.WriteLine("Error getting IP address" & otherException.ToString())
        End Try
        Return ipAdd
    End Function

    ''
    ' Makes customer universe.
    '
    ' @param CMTechPack Specifies tech pack type. Value is True if tech tech pack is CM. Value is False if tech tech pack is PM.
    Function CreateLinkedUniverse(ByRef boUser As String, ByRef boPass As String, ByRef boRep As String, ByRef FileName As String, ByRef Domain As String, _
                                  ByRef SilentInstall As Boolean, ByRef boVersion As String, ByRef boAut As String) As Boolean

        Dim DesignerApp As Designer.Application
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

        DesignerApp = Nothing
        Console.WriteLine("Creating linked universe. Please make sure the product universe has been updated and exported to the " _
                           & "repository before executing this command")
        Console.WriteLine("Repository server: " & boRep)
        Console.WriteLine("Source universe file name: " & FileName)
        Console.WriteLine("Domain (directory on BO server): " & Domain)
        Console.WriteLine("BO version: " & boVersion)
        Console.WriteLine("BO authentication: " & boAut)

        If FileName <> "" Then
            'UniverseImportName = Replace(FileName, ".unv", "")
            UniverseImportName = FileName

            Try
                DesignerApp = New Designer.Application
                DesignerApp.Visible = False
                If SilentInstall = True Then
                    DesignerApp.Interactive = False
                End If
                If (boVersion = "6.5") Then
                    DesignerApp.LoginAs(boUser, boPass, False, boRep)
                ElseIf (boVersion = "XI") Then
                    DesignerApp.Logon(boUser, boPass, boRep, boAut)
                End If
            Catch ex As Exception
                Console.WriteLine("Failed to log on to repository server " & boRep)
                Trace.WriteLine("BO Exception logging on to repository server: " + ex.ToString)
                Try
                    DesignerApp.Quit()
                Catch ee As Exception
                    Trace.WriteLine("Error closing Designer application.")
                End Try
                Return False
            End Try
            Console.WriteLine("Logged on to repository server " & boRep & " successfully.")

            retry = True
            try_count = 1
            While retry = True
                Try
                    retry = False
                    DesignerApp.Universes.Import(Domain, UniverseImportName)
                    Console.WriteLine("Universe '" & UniverseImportName & "' imported from domain '" & Domain & "' successfully.")
                    Trace.WriteLine("Universe '" & UniverseImportName & "' imported from domain '" & Domain & "' successfully.")
                Catch ex As Exception
                    Console.WriteLine("Universe '" & UniverseImportName & "' import from domain '" & Domain & "' failed. Exception: " & ex.Message)
                    Trace.WriteLine("Universe '" & UniverseImportName & "' import from domain '" & Domain & "' failed. Exception: " & ex.Message)
                    System.Threading.Thread.Sleep(2000)
                    retry = True
                    try_count += 1
                    If try_count = 2 Then
                        Return False
                    End If
                End Try
            End While
            Console.WriteLine("Imported " & UniverseImportName & " from " & Domain)

            retry = True
            try_count = 1
            While retry = True
                Try
                    retry = False
                    SrcUniv = openUniverse(boRep, FileName, Domain, DesignerApp)
                Catch ex As Exception
                    Console.WriteLine("Failed to open open universe, retrying...")
                    System.Threading.Thread.Sleep(2000)
                    retry = True
                    try_count += 1
                    If try_count = 2 Then
                        Console.WriteLine("Failed to open source universe, exiting")
                        Trace.WriteLine("Failed to open source universe, exiting")
                        Return False
                    End If
                End Try
            End While
        Else
            MsgBox("No universe filename defined, please open the universe file. ", MsgBoxStyle.Information, "Open source universe file")
            Console.WriteLine("Please open universe file:")
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
                            Console.WriteLine("Got linked universe long name: " & LongName)
                            Console.WriteLine("Got linked universe description: " & Description)
                        Catch ex As Exception
                            System.Threading.Thread.Sleep(2000)
                            retry = True
                        End Try
                    End While

                    Console.WriteLine("Adding linked universe to repository")
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
                    Console.WriteLine("Added linked universe to repository")

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
                    Console.WriteLine("Set linked universe description")

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
                    Console.WriteLine("Set linked universe name")

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
                    Console.WriteLine("Set linked universe connection")

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
                                Trace.WriteLine("Universe Control Option Exception: " & ex.Message)
                            End Try

                        Catch ex As Exception
                            System.Threading.Thread.Sleep(2000)
                            retry = True
                        End Try
                    End While
                    Console.WriteLine("Set linked universe control options")
                    System.Threading.Thread.Sleep(5000)
                    retry = True
                    try_count = 1
                    While retry = True
                        Try
                            retry = False
                            Dim LinkedUniverse As Designer.LinkedUniverse
                            LinkedUniverse = Univ.LinkedUniverses.Add(Path & "\" & Name & ".unv")
                            Trace.WriteLine("Linked to source universe '" & Path & "\" & Name & ".unv" & "' successfully.")
                            Console.WriteLine("Linked to source universe '" & Path & "\" & Name & ".unv" & "' successfully.")
                        Catch ex As Exception
                            Trace.WriteLine("Linking to universe '" & Path & "\" & Name & ".unv" & "' failed. Exception: " & ex.Message)
                            System.Threading.Thread.Sleep(2000)
                            retry = True
                            try_count += 1
                            If try_count = 2 Then
                                Return False
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

                    Console.WriteLine("Building universe contexts (copying contexts from source universe to linked universe)")
                    Call Universe_BuildContexts(Univ, SrcUniv)
                    Console.WriteLine("Finished building universe contexts")

                    UniverseName = Univ.LongName

                    retry = True
                    try_count = 1
                    While retry = True
                        Try
                            retry = False
                            Univ.SaveAs(UniverseName)
                            Trace.WriteLine("Saved linked universe '" & UniverseName & "' successfully.")
                            Console.WriteLine("Saved linked universe '" & UniverseName & "' successfully.")
                            MsgBox("Saved linked universe to: " & Univ.Path, MsgBoxStyle.OkOnly, "Saved linked universe")
                        Catch ex As Exception
                            Trace.WriteLine("Saving linked universe '" & UniverseName & "' failed. Exception: " & ex.Message)
                            Console.WriteLine("Saving linked universe '" & UniverseName & "' failed. Exception: " & ex.Message)
                            MsgBox("Failed to save linked universe. See logs for details.", MsgBoxStyle.Critical, "Error")
                            System.Threading.Thread.Sleep(2000)
                            retry = True
                            try_count += 1
                            If try_count = 2 Then
                                Return False
                            End If
                        End Try
                    End While

                    retry = True
                    While retry = True
                        Try
                            retry = False
                            Univ.Close()
                            Console.WriteLine("Closed linked universe")
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
                            Console.WriteLine("Closed source universe")
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
                            DesignerApp.Universes.Export(Domain, UniverseName)
                            Trace.WriteLine("Universe '" & UniverseName & "' exported to domain '" & Domain & "' successfully.")
                            Console.WriteLine("Exported linked universe " & UniverseName & " to domain '" & Domain & "' successfully.")
                        Catch ex As Exception
                            Trace.WriteLine("Universe '" & UniverseName & "' export to domain '" & Domain & "' failed. Exception: " & ex.Message)
                            Console.WriteLine("Universe '" & UniverseName & "' export to domain '" & Domain & "' failed. Exception: " & ex.Message)
                            System.Threading.Thread.Sleep(2000)
                            retry = True
                            try_count += 1
                            If try_count = 2 Then
                                Return False
                            End If
                        End Try
                    End While

                    Return True
    End Function

    ''
    ' Updates customer universe.
    '
    ' @param CMTechPack Specifies tech pack type. Value is True if tech tech pack is CM. Value is False if tech tech pack is PM.
    Function UpdateLinkedUniverse(ByRef boUser As String, ByRef boPass As String, ByRef boRep As String, ByRef FileName As String, ByRef Domain As String, _
                                  ByRef SilentInstall As Boolean, ByRef boVersion As String, ByRef boAut As String) As Boolean

        Dim DesignerApp As Designer.Application
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

        DesignerApp = Nothing

        Console.WriteLine("Updating linked universe. Please make sure the product universe has been updated and exported to the " _
                   & "repository before executing this command")
        Console.WriteLine("Repository server: " & boRep)
        Console.WriteLine("Source universe file name: " & FileName)
        Console.WriteLine("Domain: " & Domain)
        Console.WriteLine("BO version: " & boVersion)
        Console.WriteLine("BO authentication: " & boAut)

        If FileName <> "" Then
            Try
                DesignerApp = New Designer.Application
                DesignerApp.Visible = False
                If SilentInstall = True Then
                    DesignerApp.Interactive = False
                End If

                If (boVersion = "6.5") Then
                    DesignerApp.LoginAs(boUser, boPass, False, boRep)
                ElseIf (boVersion = "XI") Then
                    DesignerApp.Logon(boUser, boPass, boRep, boAut)
                End If
            Catch ex As Exception
                Trace.WriteLine("Failed to log on to server " & boRep)
                Trace.WriteLine("BO Exception while logging on to server: " & ex.ToString)
                Try
                    DesignerApp.Quit()
                Catch ee As Exception
                    Trace.WriteLine("Error closing Designer application.")
                End Try
                Return False
            End Try
            Console.WriteLine("Logged on to repository server " & boRep & " successfully.")

            'UniverseImportName = Replace(FileName, ".unv", "")
            UniverseImportName = FileName

            If String.Compare(FileName.Substring(1, 1), "D") = 1 And boVersion = "6.5" Then  ' This is false.
                Trace.WriteLine("Update Linked Universe not allowed, check used domain universe file (DCEXX.unv).")
                Return False
            ElseIf String.Compare(FileName.Substring(1, 3), "TP ") = 1 And boVersion = "XI" Then  ' This is false.
                Trace.WriteLine("Update Linked Universe not allowed, check used domain universe file not start CUSTOM_XXXXXX.unv.")
                Return False
            End If

            retry = True
            try_count = 1
            While retry = True
                Try
                    retry = False
                    DesignerApp.Universes.Import(Domain, UniverseImportName)
                    Trace.WriteLine("Source universe '" & UniverseImportName & "' imported from domain '" & Domain & "' successfully.")
                    Console.WriteLine("Source universe '" & UniverseImportName & "' imported from domain '" & Domain & "' successfully.")
                Catch ex As Exception
                    Trace.WriteLine("Source universe '" & UniverseImportName & "' import from domain '" & Domain & "' failed. Exception: " & ex.Message)
                    Console.WriteLine("Source universe '" & UniverseImportName & "' import from domain '" & Domain & "' failed. Exception: " & ex.Message)
                    System.Threading.Thread.Sleep(2000)
                    retry = True
                    try_count += 1
                    If try_count = 2 Then
                        Return False
                    End If
                End Try
            End While
            Console.WriteLine("Imported source universe " & UniverseImportName & " from " & Domain)

            If (boVersion = "6.5") Then
                LinkedUniverseImportName = Replace(FileName, "DC", "C")
            ElseIf (boVersion = "XI") Then
                LinkedUniverseImportName = Replace(FileName, "TP ", "")
            End If
            Console.WriteLine("Got linked universe name: " & LinkedUniverseImportName)
            Console.WriteLine("Importing linked universe from repository server")
            retry = True
            try_count = 1
            While retry = True
                Try
                    retry = False
                    DesignerApp.Universes.Import(Domain, LinkedUniverseImportName)
                    Trace.WriteLine("Universe '" & LinkedUniverseImportName & "' imported from domain '" & Domain & "' successfully.")
                    Console.WriteLine("Universe '" & LinkedUniverseImportName & "' imported from domain '" & Domain & "' successfully.")
                Catch ex As Exception
                    Trace.WriteLine("Universe '" & LinkedUniverseImportName & "' import from domain '" & Domain & "' failed. Exception: " & ex.Message)
                    Console.WriteLine("Universe '" & LinkedUniverseImportName & "' import from domain '" & Domain & "' failed. Exception: " & ex.Message)
                    System.Threading.Thread.Sleep(2000)
                    retry = True
                    try_count += 1
                    If try_count = 2 Then
                        Return False
                    End If
                End Try
            End While

            retry = True
            try_count = 1
            While retry = True
                Try
                    retry = False
                    ' Open the source universe file:
                    SrcUniv = openUniverse(boRep, FileName, Domain, DesignerApp)
                Catch ex As Exception
                    System.Threading.Thread.Sleep(2000)
                    retry = True
                    try_count += 1
                    If try_count = 2 Then
                        Return False
                    End If
                End Try
            End While

            retry = True
            try_count = 1
            While retry = True
                Try
                    retry = False
                    ' Open the linked universe file:
                    Univ = openUniverse(boRep, LinkedUniverseImportName, Domain, DesignerApp)
                Catch ex As Exception
                    System.Threading.Thread.Sleep(2000)
                    retry = True
                    try_count += 1
                    If try_count = 2 Then
                        Return False
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
                            Console.WriteLine("Got linked universe long name: " & LongName)
                            Console.WriteLine("Got linked universe description: " & Description)
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
                    Console.WriteLine("Set linked universe description")

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
                    Console.WriteLine("Set linked universe connection")

                    'Console.WriteLine("Univ: " & Univ.Name)
                    'Console.WriteLine("SrcUniv: " & SrcUniv.Name)

                    'TODO
                    Dim extraJoins As UnivJoinsTPIde
                    Dim Count As Integer
                    Dim Found As Boolean
                    Dim First As Boolean
                    Dim Cntxt As Designer.Context
                    Dim extraJoin As UnivJoinsTPIde.UnivJoin
                    Dim ListedContexts As String
                    Console.WriteLine("Updating linked universe contexts and joins...")
                    extraJoins = Universe_UpdateContexts(Univ, SrcUniv)
                    Console.WriteLine("Finished updating linked universe contexts and joins")

                    If extraJoins Is Nothing Then
                        Trace.WriteLine("Stopped on exception.")
                        Return False
                    End If

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
                                Trace.WriteLine(" ")
                                Trace.WriteLine("Linked universe contains following extra contexts:")
                                Trace.WriteLine(extraJoin.Contexts)
                                First = False
                            Else
                                Trace.WriteLine(extraJoin.Contexts)
                            End If
                            ListedContexts &= extraJoin.Contexts & ","
                        End If
                    Next Count

                    First = True
                    For Count = 1 To extraJoins.Count
                        extraJoin = extraJoins.Item(Count)
                        If First = True Then
                            Trace.WriteLine("Linked universe contains following extra joins:")
                            Trace.WriteLine("Join '" & extraJoin.Expression & "' in context '" & extraJoin.Contexts & "'.")
                            First = False
                        Else
                            Trace.WriteLine("Join '" & extraJoin.Expression & "' in context '" & extraJoin.Contexts & "'.")
                        End If
                    Next Count
                    If First = False Then
                        Trace.WriteLine(" ")
                    End If

                    retry = True
                    try_count = 1
                    While retry = True
                        Try
                            retry = False
                            Univ.SaveAs(LinkedUniverseImportName)
                            Trace.WriteLine("Saved universe '" & LinkedUniverseImportName & "' successfully.")
                            Console.WriteLine("Saved linked universe '" & LinkedUniverseImportName & "' to " & Univ.Path & " successfully.")
                            MsgBox("Saved linked universe to: " & Univ.Path, MsgBoxStyle.OkOnly, "Saved linked universe")
                        Catch ex As Exception
                            Trace.WriteLine("Saving universe '" & LinkedUniverseImportName & "' failed. Exception: " & ex.Message)
                            Console.WriteLine("Saving universe '" & LinkedUniverseImportName & "' failed. Exception: " & ex.Message)
                            MsgBox("Saved linked universe to: " & Univ.Path, MsgBoxStyle.OkOnly, "Saved linked universe")
                            System.Threading.Thread.Sleep(2000)
                            retry = True
                            try_count += 1
                            If try_count = 2 Then
                                Return False
                            End If
                        End Try
                    End While

                    retry = True
                    While retry = True
                        Try
                            retry = False
                            Univ.Close()
                            Console.WriteLine("Closed linked universe")
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
                            Console.WriteLine("Closed source universe")
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
                            DesignerApp.Universes.Export(Domain, LinkedUniverseImportName)
                            Trace.WriteLine("Universe '" & LinkedUniverseImportName & "' exported to domain '" & Domain & "' successfully.")
                            Console.WriteLine("Universe '" & LinkedUniverseImportName & "' exported to domain '" & Domain & "' successfully.")
                        Catch ex As Exception
                            Trace.WriteLine("Universe '" & LinkedUniverseImportName & "' export to domain '" & Domain & "' failed. Exception: " & ex.Message)
                            Console.WriteLine("Universe '" & LinkedUniverseImportName & "' export to domain '" & Domain & "' failed. Exception: " & ex.Message)
                            System.Threading.Thread.Sleep(2000)
                            retry = True
                            try_count += 1
                            If try_count = 2 Then
                                Return False
                            End If
                        End Try
                    End While

                    Return True

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

        extraJoins = New UnivJoinsTPIde

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
                Console.WriteLine("Add to linked universes failed. Exception: " & ex.Message)
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
        Dim extraJoin As UnivJoinsTPIde.UnivJoin
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
                'Console.WriteLine(ex.ToString)
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
                        'Console.WriteLine(ex.ToString)
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
        Dim inCompatible As UnivIncombatiblesTPIDE.UnivIncombatible
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
                Console.WriteLine(ex.ToString)
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        Return Value
    End Function

End Class
