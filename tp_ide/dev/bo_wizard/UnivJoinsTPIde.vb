Option Strict Off

Imports System.Collections

''
'  UnivJoins class is a collection of UnivJoin classes
'
Public Class UnivJoinsTPIde
    Private measurements() As String
    Private measurements_count As Long
    Private basic_measurements() As String
    Private basic_measurements_count As Long
    Private JoinsArray() As String
    Private JoinCount As Integer
    Private _joins As System.Collections.ArrayList = New System.Collections.ArrayList
    Private tpUtilities As TPUtilitiesTPIde
    Private rankMTs As ArrayList
    Private tpConn As System.Data.Odbc.OdbcConnection
    Private techpackName As String
    Private techpackIde As String
    Private techpackVersion As String

    Private mtypes As MeasurementTypesTPIde
    Private rankBusyHourJoins As ArrayList
    Private listOfRankBHJoinExpressions As ArrayList
    Private enableRankBusyHourFunctionality As Boolean

    Private rankBHBaseTypes As Hashtable

    ''
    ' Gets/Sets the value of rankBusyHourJoins
    Public Property RankBHJoins() As ArrayList
        Get
            RankBHJoins = rankBusyHourJoins
        End Get

        Set(ByVal Value As ArrayList)
            rankBusyHourJoins = Value
        End Set
    End Property

    ''
    '  Gets count of UnivJoin classes in UnivJoins class
    '
    ' @param Index Specifies the index in the UnivJoins class
    ' @return Count of UnivJoin classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _joins Is Nothing) Then
                Return _joins.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets UnivJoin class from UnivJoins class based on given index.
    '
    ' @param Index Specifies the index in the UnivJoins class
    ' @return Reference to UnivJoin
    Public ReadOnly Property Item(ByVal Index As Integer) As UnivJoin
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_joins.Item(Index - 1), UnivJoin)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds UnivJoin class to UnivJoins class
    '
    ' @param ValueIn Specifies reference to UnivJoin
    Public Sub AddItem(ByVal ValueIn As UnivJoin)

        If (Not _joins Is Nothing) Then
            _joins.Add(ValueIn)
        End If

    End Sub

    ''
    '  UnivJoin class defines universe's joins.
    '
    Public Class UnivJoin
        Private m_Expression As String
        Private m_Cardinality As Integer
        Private m_Contexts As String
        Private m_ExcludedContexts As String
        Private m_FirstTable As String
        Private m_SecondTable As String
        Private m_UniverseExtension As String
        Private Enabled As Boolean = True

        Public Property FirstTable()
            Get
                FirstTable = m_FirstTable
            End Get

            Set(ByVal Value)
                m_FirstTable = Value
            End Set

        End Property
        Public Property SecondTable()
            Get
                SecondTable = m_SecondTable
            End Get

            Set(ByVal Value)
                m_SecondTable = Value
            End Set

        End Property
        ''
        ' Gets and sets value for Expression parameter. 
        ' Expression defines join's expression.
        '
        ' @param Value Specifies value of Expression parameter
        ' @return Value of Expression parameter
        Public Property Expression()
            Get
                Expression = m_Expression
            End Get

            Set(ByVal Value)
                m_Expression = Value
            End Set

        End Property

        ''
        ' Gets and sets value for Cardinality parameter. 
        ' Cardinality defines join's cardinality.
        '
        ' @param Value Specifies value of Cardinality parameter
        ' @return Value of Cardinality parameter
        Public Property Cardinality()
            Get
                Cardinality = m_Cardinality
            End Get

            Set(ByVal Value)
                If Value = "n_to_1" Then
                    m_Cardinality = Designer.DsCardinality.dsManyToOneCardinality
                End If
                If Value = "1_to_n" Then
                    m_Cardinality = Designer.DsCardinality.dsOneToManyCardinality
                End If
                If Value = "1_to_1" Then
                    m_Cardinality = Designer.DsCardinality.dsOneToOneCardinality
                End If
                If Value = "n_to_n" Then
                    m_Cardinality = Designer.DsCardinality.dsManyToManyCardinality
                End If
            End Set

        End Property

        ''
        ' Gets and sets value for Contexts parameter. 
        ' Contexts defines join's contexts.
        '
        ' @param Value Specifies value of Contexts parameter
        ' @return Value of Contexts parameter
        Public Property Contexts()
            Get
                Contexts = m_Contexts
            End Get

            Set(ByVal Value)
                m_Contexts = Value
            End Set

        End Property

        ''
        ' Gets and sets value for ExcludedContexts parameter. 
        ' ExcludedContexts defines join's excluded contexts.
        '
        ' @param Value Specifies value of ExcludedContexts parameter
        ' @return Value of ExcludedContexts parameter
        Public Property ExcludedContexts()
            Get
                ExcludedContexts = m_ExcludedContexts
            End Get

            Set(ByVal Value)
                m_ExcludedContexts = Value
            End Set

        End Property

        ''
        ' Gets and sets value for ExcludedContexts parameter. 
        ' ExcludedContexts defines join's excluded contexts.
        '
        ' @param Value Specifies value of ExcludedContexts parameter
        ' @return Value of ExcludedContexts parameter
        Public Property Extension() As String
            Get
                Extension = m_UniverseExtension
            End Get

            Set(ByVal Value As String)
                m_UniverseExtension = Extension
            End Set

        End Property
    End Class

    ''
    ' Gets joins for measurement types. 
    '
    ' @param tp_name Specifies technology package name
    ' @param mts Specifies reference to MeasurementTypes
    ' @param conn Specifies reference to ODBC Connection
    ' @param dbCommand Specifies reference to ODBC Command
    ' @param dbReader Specifies reference to ODBC DataReader
    Public Function getJoins(ByRef tp_name As String, ByRef mts As MeasurementTypesTPIde, ByRef conn As System.Data.Odbc.OdbcConnection, _
                             ByRef dbCommand As System.Data.Odbc.OdbcCommand, ByRef dbReader As System.Data.Odbc.OdbcDataReader, _
                             ByRef TechPackTPIde As String, ByRef extendedCountObject As Boolean, _
                             ByVal TPVersion As String, ByVal mainTechPack As Boolean, ByVal UniverseNamextension As String, _
                             ByVal rankBusyHourFunctionality As Boolean) As Boolean

        Dim JoinExpr As String

        Dim StartT As String
        Dim StartL As String
        Dim StartC As String
        Dim EndT As String
        Dim EndL As String
        Dim EndC As String
        Dim tmp_StartT As String
        Dim tmp_StartL As String
        Dim tmp_StartC As String
        Dim tmp_EndT As String
        Dim tmp_EndL As String
        Dim tmp_EndC As String

        Dim Cardinality As String
        Dim Contexts As String
        Dim ExcludedContexts As String
        Dim UniverseExtension As String
        Dim count As Integer
        Dim mt As MeasurementTypesTPIde.MeasurementType

        Dim bh_mts As MeasurementTypesTPIde
        Dim bh_mt As MeasurementTypesTPIde.MeasurementType
        Dim bh_count As Integer
        Dim First As Boolean
        Dim bhtables As String

        Dim cnts As CounterKeysTPIde
        Dim cnt As CounterKeysTPIde.CounterKey
        Dim cnt_count As Integer
        Dim element_column As String

        Dim BusyHourObjects() As String
        Dim bhObjectCount As Integer

        Dim unvjoin As String

        ' Check if rank busy hour functionality should be enabled:
        enableRankBusyHourFunctionality = rankBusyHourFunctionality
        listOfRankBHJoinExpressions = New ArrayList()

        unvjoin = "SELECT SUBSTR(SOURCETABLE,1,8000),SUBSTR(SOURCETABLE,8001,8000),SUBSTR(SOURCETABLE,16001,8000),SUBSTR(SOURCETABLE,24001,8000)" & _
        ",SOURCELEVEL,SOURCECOLUMN," & _
        "SUBSTR(TARGETTABLE,1,8000),SUBSTR(TARGETTABLE,8001,8000),SUBSTR(TARGETTABLE,16001,8000),SUBSTR(TARGETTABLE,24001,8000)" & _
        ",TARGETLEVEL,TARGETCOLUMN,EXPRESSION,CARDINALITY," & _
        "SUBSTR(CONTEXT,1,8000),SUBSTR(CONTEXT,8001,8000),SUBSTR(CONTEXT,16001,8000),SUBSTR(CONTEXT,24001,8000)," & _
        "SUBSTR(EXCLUDEDCONTEXTS,1,8000),SUBSTR(EXCLUDEDCONTEXTS,8001,8000),SUBSTR(EXCLUDEDCONTEXTS,16001,8000),SUBSTR(EXCLUDEDCONTEXTS,24001,8000), " & _
        "SOURCETABLE, UNIVERSEEXTENSION FROM Universejoin where versionid ='" & TechPackTPIde & "'"

        'dbCommand = New System.Data.Odbc.OdbcCommand("SELECT * FROM [Universe joins$]", conn)
        dbCommand = New System.Data.Odbc.OdbcCommand(unvjoin, conn)

        tpUtilities = New TPUtilitiesTPIde()
        rankMTs = tpUtilities.getRankMeasurementTypes(mts)
        tpConn = conn
        techpackName = tp_name
        techpackIde = TechPackTPIde
        techpackVersion = TPVersion

        mtypes = mts
        rankBusyHourJoins = New ArrayList()

        ' Get rank bh base types:
        If (rankBHBaseTypes Is Nothing And enableRankBusyHourFunctionality) Then
            rankBHBaseTypes = New Hashtable
            rankBHBaseTypes = getBHTargetTypes()
        End If

        Dim universeJoinCount As Integer
        universeJoinCount = 0

        Try
            If dbReader.IsClosed = False Then
                dbReader.Close()
            End If
            dbReader = dbCommand.ExecuteReader()
        Catch ex As Exception
            Trace.WriteLine("Database Exception: " & ex.ToString)
            Return False
        End Try

        Try
            While (dbReader.Read())
                If dbReader.GetValue(0).ToString() = "" Then
                    Trace.WriteLine("Error: No join data read for " & TechPackTPIde)
                    Exit While
                Else
                    If dbReader.GetValue(0).ToString() <> "" Then
                        Try
                            If dbReader.IsDBNull(0) = False Then
                                tmp_StartT = Trim(dbReader.GetString(0) + dbReader.GetString(1) + dbReader.GetString(2) + dbReader.GetString(3))
                            Else
                                tmp_StartT = ""
                            End If
                            tmp_StartL = Trim(dbReader.GetValue(4).ToString())
                            tmp_StartC = Trim(dbReader.GetValue(5).ToString())
                            If dbReader.IsDBNull(6) = False Then
                                tmp_EndT = Trim(dbReader.GetString(6) + dbReader.GetString(7) + dbReader.GetString(8) + dbReader.GetString(9))
                            Else
                                tmp_EndT = ""
                            End If
                            tmp_EndL = Trim(dbReader.GetValue(10).ToString())
                            tmp_EndC = Trim(dbReader.GetValue(11).ToString())
                            Cardinality = Trim(dbReader.GetValue(13).ToString())

                            If dbReader.IsDBNull(14) = False Then
                                Contexts = Trim(dbReader.GetString(14) + dbReader.GetString(15) + dbReader.GetString(16) + dbReader.GetString(17))
                            Else
                                Contexts = ""
                            End If
                            ' JTS 22.9.2008
                            'Contexts = Trim(dbReader.GetString(14) + dbReader.GetString(15) + dbReader.GetString(16) + dbReader.GetString(17))

                            If dbReader.IsDBNull(18) = False Then
                                ExcludedContexts = Trim(dbReader.GetString(18) + dbReader.GetString(19) + dbReader.GetString(20) + dbReader.GetString(21))
                            Else
                                ExcludedContexts = ""
                            End If

                            If dbReader.IsDBNull(23) = False Then
                            UniverseExtension = Trim(dbReader.GetString(23))
                            Else
                                Trace.WriteLine("UniverseExtension column was NULL in table Universejoin for: " & TechPackTPIde)
                                Console.WriteLine("UniverseExtension column was NULL in table Universejoin for: " & TechPackTPIde)
                                UniverseExtension = ""
                            End If


                        Catch ex As Exception
                            Trace.WriteLine("Error reading join data from database: " & ex.ToString())
                        End Try

                        Dim extensionOK As Boolean = checkExtension(UniverseExtension, UniverseNamextension)

                        If (extensionOK) Then
                            Try
                                If InStrRev(tmp_StartT, "(OBJECTBH_MT)") > 0 Then
                                    bh_mts = mts
                                    For count = 1 To mts.Count
                                        mt = mts.Item(count)
                                        If mt.RankTable = True AndAlso mt.ObjectBusyHours <> "" Then
                                            First = True
                                            For bh_count = 1 To bh_mts.Count
                                                bh_mt = bh_mts.Item(bh_count)
                                                If bh_mt.ObjectBusyHours <> "" Then
                                                    'BusyHourObjects = Split(mt.ObjectBusyHours, ",")
                                                    BusyHourObjects = Split(bh_mt.ObjectBusyHours, ",")
                                                    For bhObjectCount = 0 To UBound(BusyHourObjects)
                                                        If bh_mt.RankTable = False AndAlso BusyHourObjects(bhObjectCount) = mt.ObjectBusyHours Then
                                                            If First = True Then
                                                                bhtables = "DC." & bh_mt.TypeName
                                                                First = False
                                                            Else
                                                                bhtables &= "," & "DC." & bh_mt.TypeName
                                                            End If
                                                        End If
                                                    Next bhObjectCount
                                                End If
                                            Next bh_count
                                            StartT = Replace(tmp_StartT, "(OBJECTBH_MT)", bhtables)

                                            ' EndT = Replace(tmp_EndT, "(DIM_OBJECTRANKMT)", Replace(mt.TypeName, "DC_", "DIM_"))
                                            If (mt.TypeName.StartsWith("DC_")) Then
                                                EndT = Replace(tmp_EndT, "(DIM_OBJECTRANKMT)", Replace(mt.TypeName, "DC_", "DIM_", , 1))
                                               
                                            ElseIf (mt.TypeName.StartsWith("PM_")) Then
                                                EndT = Replace(tmp_EndT, "(DIM_OBJECTRANKMT)", Replace(mt.TypeName, "PM_", "DIM_"))
                                            ElseIf (mt.TypeName.StartsWith("CM_")) Then
                                                EndT = Replace(tmp_EndT, "(DIM_OBJECTRANKMT)", Replace(mt.TypeName, "CM_", "DIM_"))
                                            ElseIf (mt.TypeName.StartsWith("CUSTOM_")) Then
                                                EndT = Replace(tmp_EndT, "(DIM_OBJECTRANKMT)", Replace(mt.TypeName, "CUSTOM_", "DIM_"))
                                            End If
                                            If tmp_StartL <> "RAW/COUNT" Then
                                                StartL = tmp_StartL
                                            Else
                                                If mt.CreateCountTable = True Then
                                                    StartL = "COUNT"
                                                Else
                                                    StartL = "RAW"
                                                End If
                                            End If
                                            StartC = tmp_StartC
                                            EndL = tmp_EndL
                                            EndC = tmp_EndC
                                            If makeJoins(tp_name, StartT, StartL, StartC, EndT, EndL, EndC, Cardinality, Contexts, ExcludedContexts, extendedCountObject) = False Then
                                                Return False
                                            End If
                                        End If
                                    Next count
                                ElseIf InStrRev(tmp_StartT, "(ELEMENTRANKMT)") > 0 Then
                                    For count = 1 To mts.Count
                                        mt = mts.Item(count)
                                        If mt.RankTable = True Then ' AndAlso mt.ElementBusyHours = True Then '(?)
                                            StartT = Replace(tmp_StartT, "(ELEMENTRANKMT)", mt.TypeName)
                                            ' EndT = Replace(tmp_EndT, "(DIM_ELEMENTRANKMT)", Replace(mt.TypeName, "DC_", "DIM_"))
                                            If (mt.TypeName.StartsWith("DC_")) Then
                                                EndT = Replace(tmp_EndT, "(DIM_ELEMENTRANKMT)", Replace(mt.TypeName, "DC_", "DIM_", , 1))
                                            ElseIf (mt.TypeName.StartsWith("PM_")) Then
                                                EndT = Replace(tmp_EndT, "(DIM_ELEMENTRANKMT)", Replace(mt.TypeName, "PM_", "DIM_"))
                                            ElseIf (mt.TypeName.StartsWith("CM_")) Then
                                                EndT = Replace(tmp_EndT, "(DIM_ELEMENTRANKMT)", Replace(mt.TypeName, "CM_", "DIM_"))
                                            ElseIf (mt.TypeName.StartsWith("CUSTOM_")) Then
                                                EndT = Replace(tmp_EndT, "(DIM_ELEMENTRANKMT)", Replace(mt.TypeName, "CUSTOM_", "DIM_"))
                                            End If
                                            If tmp_StartL <> "RAW/COUNT" Then
                                                StartL = tmp_StartL
                                            Else
                                                If mt.CreateCountTable = True Then
                                                    StartL = "COUNT"
                                                Else
                                                    StartL = "RAW"
                                                End If
                                            End If
                                            StartL = tmp_StartL
                                            StartC = tmp_StartC
                                            EndL = tmp_EndL
                                            EndC = tmp_EndC
                                            If mt.ElementBusyHours = True Then
                                                ' Add for both ELEMBH and RANKBH:
                                                If makeJoins(tp_name, StartT, StartL, StartC, EndT, EndL, EndC, Cardinality, Contexts, ExcludedContexts, extendedCountObject) = False Then
                                                    Return False
                                                End If
                                                If (enableRankBusyHourFunctionality = True) Then
                                                    If makeJoins(tp_name, StartT, StartL, StartC, EndT, EndL, EndC, Cardinality, "RANKBH", ExcludedContexts, extendedCountObject) = False Then
                                                        Return False
                                                    End If
                                                End If
                                            ElseIf mt.ElementBusyHours = False Then
                                                ' Only add for RANKBH:
                                                If (enableRankBusyHourFunctionality = True) Then
                                                    If makeJoins(tp_name, StartT, StartL, StartC, EndT, EndL, EndC, Cardinality, "RANKBH", ExcludedContexts, extendedCountObject) = False Then
                                                        Return False
                                                    End If
                                                End If
                                            End If
                                        End If
                                    Next count
                                ElseIf InStrRev(tmp_StartT, "(ELEMENTBHMT)") > 0 Then
                                    bh_mts = mts
                                    For count = 1 To mts.Count
                                        mt = mts.Item(count)

                                        Try
                                            If mt.RankTable = True Then
                                                Dim targetTypesForMt As ArrayList
                                                If (enableRankBusyHourFunctionality = True) Then
                                                    ' get target types for busy hour
                                                    targetTypesForMt = tpUtilities.getBHTargetTypes(techpackVersion, mt, tpConn)
                                                End If

                                                cnts = mt.CounterKeys
                                                For cnt_count = 1 To cnts.Count
                                                    cnt = cnts.Item(cnt_count)
                                                    If cnt.Element = 1 Then
                                                        element_column = cnt.CounterKeyName
                                                        Exit For
                                                    End If
                                                Next cnt_count

                                                First = True
                                                For bh_count = 1 To bh_mts.Count
                                                    bh_mt = bh_mts.Item(bh_count)
                                                    If mt.ElementBusyHours = True Then
                                                        If bh_mt.RankTable = False AndAlso bh_mt.ElementBusyHours = mt.ElementBusyHours Then
                                                            StartT = Replace(tmp_StartT, "(ELEMENTBHMT)", bh_mt.TypeName)
                                                            EndT = Replace(tmp_EndT, "(ELEMENTRANKMT)", mt.TypeName)
                                                            StartC = Replace(tmp_StartC, "(ELEMENTCOLUMN)", element_column)
                                                            EndC = Replace(tmp_EndC, "(ELEMENTCOLUMN)", element_column)
                                                            If tmp_StartL <> "RAW/COUNT" Then
                                                                StartL = tmp_StartL
                                                            Else
                                                                If bh_mt.CreateCountTable = True Then
                                                                    StartL = "COUNT"
                                                                Else
                                                                    StartL = "RAW"
                                                                End If
                                                            End If
                                                            EndL = tmp_EndL
                                                            If makeJoins(tp_name, StartT, StartL, StartC, EndT, EndL, EndC, Cardinality, Contexts, ExcludedContexts, extendedCountObject) = False Then
                                                                Return False
                                                            End If

                                                            If (enableRankBusyHourFunctionality = True) Then
                                                                ' We also need this join in the RANKBH context, not just the ELEMBH:
                                                            'If makeJoins(tp_name, StartT, StartL, StartC, EndT, EndL, EndC, Cardinality, "RANKBH", ExcludedContexts, extendedCountObject) = False Then
                                                            'Return False
                                                            'End If
                                                            End If
                                                        End If
                                                    'ElseIf (mt.ElementBusyHours = False And enableRankBusyHourFunctionality = True) Then
                                                        ' This is for RANKBH tables that are not elembh (e.g. BSCBH, AAL2APBH etc.):
                                                    ' Dim rankBHJoinNeeded As Boolean
                                                    ' rankBHJoinNeeded = checkIfRankBHJoinNeeded(targetTypesForMt, bh_mt, mt)
                                                    'If (rankBHJoinNeeded = True) Then
                                                    'setupTableAndColumnValues(bh_mt, mt, StartT, tmp_StartT, StartC, tmp_StartC, EndT, tmp_EndT, _
                                                    '                     EndC, tmp_EndC, StartL, tmp_StartL, EndL, tmp_EndL, element_column)
                                                            ' We need this join in the RANKBH context:
                                                    'If makeJoins(tp_name, StartT, StartL, StartC, EndT, EndL, EndC, Cardinality, "RANKBH", ExcludedContexts, extendedCountObject) = False Then
                                                    'Return False
                                                    '  End If
                                                    ' End If
                                                    End If
                                                Next bh_count
                                            End If
                                        Catch ex As Exception
                                            Trace.WriteLine(ex.ToString())
                                        End Try
                                    Next count
                                Else
                                    StartT = tmp_StartT
                                    EndT = tmp_EndT
                                    StartL = tmp_StartL
                                    StartC = tmp_StartC
                                    EndL = tmp_EndL
                                    EndC = tmp_EndC
                                    If makeJoins(tp_name, StartT, StartL, StartC, EndT, EndL, EndC, Cardinality, Contexts, ExcludedContexts, extendedCountObject) = False Then
                                        Return False
                                    End If
                                End If
                            Catch ex As Exception
                                Trace.WriteLine("Error adding join to universe: " & ex.ToString())
                            End Try
                        End If
                    End If
                End If
            End While
        Catch ex As Exception
            Trace.WriteLine("Exception on reading join information: " & ex.ToString)
            Return False
        End Try
        dbReader.Close()
        dbCommand.Dispose()

        ' Add extra joins for rank busy hours:
        If (enableRankBusyHourFunctionality = True) Then
            addRankBusyHourJoins()
        End If
        Return True
    End Function

    ''' Checks the extension of the current universe against the extension defined for the join.
    ''' 
    '''@param extFromDatabase   The extension string read from the database for this join (the extensions the user has entered).
    '''@param currentExtension  The extension of the current universe.
    '''@returns True if the join should be added to the current universe.
    Public Function checkExtension(ByVal extFromDatabase As String, ByVal currentExtension As String) As Boolean

        Dim returnValue As Boolean = False

        If (extFromDatabase Is Nothing OrElse extFromDatabase.ToUpper() = "ALL" OrElse extFromDatabase.ToUpper() = "") Then
            ' Not defined, or ALL returns true:
            returnValue = True
        ElseIf (currentExtension Is Nothing OrElse currentExtension = "") Then
            ' In this case there is only one universe, so all joins can be added:
            returnValue = True
        Else
            Dim extensions() As String = extFromDatabase.Split(",")            
            If (extensions.Length > 1) Then
                ' If anything matches return true:
                For Each ext As String In extensions
                    If (ext.ToUpper.Trim = currentExtension.ToUpper OrElse ext.ToUpper.Trim = "ALL" OrElse ext.ToUpper().Trim() = "") Then
                        returnValue = True
                        Exit For
                    End If
                Next ext
            ElseIf (extensions.Length = 1) Then
                ' If single value matches return true:
                If (extensions(0).ToUpper.Trim = currentExtension.ToUpper OrElse extensions(0).ToUpper.Trim = "ALL" OrElse extensions(0).ToUpper().Trim() = "") Then
                    returnValue = True
                End If
            End If
        End If
        Return returnValue
    End Function

    ''
    ' Checks if a rank busy hour join should be created.
    ' 
    '@param targetTypesForMt The target types for the busy hour.
    '@param bh_mt The 
    '@param mt
    '@returns True if a rank BH join is needed.
    Public Function checkIfRankBHJoinNeeded(ByVal targetTypesForMt As ArrayList, ByVal bh_mt As MeasurementTypesTPIde.MeasurementType, _
                                            ByVal mt As MeasurementTypesTPIde.MeasurementType) As Boolean
        Dim isATargetType As Boolean
        isATargetType = False
        Dim returnValue As Boolean
        returnValue = False

        ' check if bh_mt is one of these target types
        If (targetTypesForMt.Contains(bh_mt.TypeName)) Then
            isATargetType = True
        End If

        ' if it is, add in the join:
        If bh_mt.RankTable = False AndAlso isATargetType = True Then
            returnValue = True
        Else
            returnValue = False
        End If
        Return returnValue
    End Function

    ''
    ' Sets up the table and column values for creating a RANKBH join.
    ' 
    '@param bh_mt The busy hour measurement type.
    '@param mt 
    '@param StartT
    '@param tmp_StartT
    '@param StartC
    '@param tmp_StartC
    '@param EndT
    '@param tmp_EndT
    '@param EndC
    '@param tmp_EndC
    '@param StartL
    '@param tmp_StartL
    '@param EndL
    '@param tmp_EndL
    '@param element_column
    '@returns returnValue True if join needs to be created.
    Public Sub setupTableAndColumnValues(ByVal bh_mt As MeasurementTypesTPIde.MeasurementType, ByVal mt As MeasurementTypesTPIde.MeasurementType, _
                                         ByRef StartT As String, ByRef tmp_StartT As String, ByRef StartC As String, ByRef tmp_StartC As String, _
                                      ByRef EndT As String, ByRef tmp_EndT As String, ByRef EndC As String, ByRef tmp_EndC As String, _
                                      ByRef StartL As String, ByRef tmp_StartL As String, ByRef EndL As String, ByRef tmp_EndL As String, _
                                      ByRef element_column As String)
        StartT = Replace(tmp_StartT, "(ELEMENTBHMT)", bh_mt.TypeName)
        EndT = Replace(tmp_EndT, "(ELEMENTRANKMT)", mt.TypeName)
        StartC = Replace(tmp_StartC, "(ELEMENTCOLUMN)", element_column)
        EndC = Replace(tmp_EndC, "(ELEMENTCOLUMN)", element_column)
        If tmp_StartL <> "RAW/COUNT" Then
            StartL = tmp_StartL
        Else
            If bh_mt.CreateCountTable = True Then
                StartL = "COUNT"
            Else
                StartL = "RAW"
            End If
        End If
        EndL = tmp_EndL
    End Sub

    ''
    ' Adds the rank busy hour joins to the main collection of joins.
    ' The rank busy hour joins are held in rankBusyHourJoins. They are added into _joins, 
    ' which is the main list of joins.
    Public Sub addRankBusyHourJoins()
        Dim join As UnivJoinsTPIde.UnivJoin
        join = New UnivJoinsTPIde.UnivJoin
        For Each join In rankBusyHourJoins
            AddItem(join)
        Next
    End Sub

    Public Function makeJoins(ByRef tp_name As String, ByRef StartTables As String, ByRef StartLevels As String, ByRef StartColumns As String, _
                              ByRef EndTables As String, ByRef EndLevels As String, ByRef EndColumns As String, ByRef Cardinality As String, _
                              ByRef Contexts As String, ByRef ExcludedContexts As String, ByRef extendedCountObject As Boolean) As Boolean

        Dim JoinExpr As String
        JoinExpr = ""

        'placeholder for extended COUNT functionality
        If extendedCountObject = True Then
            If InStrRev(StartLevels, "COUNT") > 0 Then
                StartLevels = StartLevels & ",DELTA"
            End If
            If InStrRev(EndLevels, "COUNT") > 0 Then
                EndLevels = EndLevels & ",DELTA"
            End If
        End If
        'placeholder for extended COUNT functionality

        CreateJoinExpression(StartTables, StartLevels, StartColumns, EndTables, EndLevels, EndColumns, extendedCountObject, Cardinality, Contexts, ExcludedContexts)
        For JoinCount = 0 To UBound(JoinsArray)
            Dim univ_join = New UnivJoinsTPIde.UnivJoin
            univ_join.Expression = JoinsArray(JoinCount)

            If (tp_name.StartsWith("DC_")) Then 'JTS 16.9.2008
                JoinExpr = Replace(univ_join.Expression, "(TPNAME)", Replace(tp_name, "DC_", ""))
            ElseIf (tp_name.StartsWith("PM_")) Then
                JoinExpr = Replace(univ_join.Expression, "(TPNAME)", Replace(tp_name, "PM_", ""))
            ElseIf (tp_name.StartsWith("CM_")) Then
                JoinExpr = Replace(univ_join.Expression, "(TPNAME)", Replace(tp_name, "CM_", ""))
            ElseIf (tp_name.StartsWith("CUSTOM_")) Then
                JoinExpr = Replace(univ_join.Expression, "(TPNAME)", Replace(tp_name, "CUSTOM_", ""))
            Else
                ' Keep the original join expression:
                JoinExpr = univ_join.Expression
            End If

            univ_join.Expression = JoinExpr
            If Cardinality <> "n_to_1" AndAlso Cardinality <> "1_to_n" AndAlso Cardinality <> "1_to_1" AndAlso Cardinality <> "n_to_n" Then
                Trace.WriteLine("Cardinality '" & Cardinality & "' for '" & univ_join.Expression & "' is not one of the supported: 'n_to_1', '1_to_n', '1_to_1', 'n_to_n'.")
                'Return False
            End If
            univ_join.Cardinality = Cardinality
            univ_join.Contexts = Contexts
            univ_join.ExcludedContexts = ExcludedContexts
            AddItem(univ_join)
        Next JoinCount

        Return True
    End Function
    'add join between *DC_VECTOR and DC_RELEASE
    Public Sub getVectorJoins(ByRef mts As MeasurementTypesTPIde, ByRef extendedCountObject As Boolean)

        Dim VectorTable As String
        Dim JoinExpr As String
        Dim count As Integer
        Dim cnt_count As Integer
        Dim cnts As CountersTPIde
        Dim cnt As CountersTPIde.Counter
        Dim mt As MeasurementTypesTPIde.MeasurementType
        Dim tpUtilities As New TPUtilitiesTPIde

        For count = 1 To mts.Count
            mt = mts.Item(count)
            cnts = mts.Item(count).Counters
            For cnt_count = 1 To cnts.Count
                cnt = cnts.Item(cnt_count)
                If cnt.CounterType = "VECTOR" AndAlso mt.VectorSupport = True Then
                    'Check for whether vector counter has range or not.
                    If (tpUtilities.isVectorRangePresent(cnt.MeasurementTypeID, cnt.CounterName, tpConn)) Then
                    ' VectorTable = Replace(cnt.TypeName, "DC_", "DIM_") & "_" & cnt.CounterName
                    If (cnt.TypeName.StartsWith("DC_")) Then
                            VectorTable = Replace(cnt.TypeName, "DC_", "DIM_", , 1) & "_" & cnt.CounterName
                    ElseIf (cnt.TypeName.StartsWith("PM_")) Then
                        VectorTable = Replace(cnt.TypeName, "PM_", "DIM_") & "_" & cnt.CounterName
                    ElseIf (cnt.TypeName.StartsWith("CM_")) Then
                        VectorTable = Replace(cnt.TypeName, "CM_", "DIM_") & "_" & cnt.CounterName
                    ElseIf (cnt.TypeName.StartsWith("CUSTOM_")) Then
                        VectorTable = Replace(cnt.TypeName, "CUSTOM_", "DIM_") & "_" & cnt.CounterName
                    End If
                    CreateJoinExpression("DC." & cnt.TypeName, "All", "DCVECTOR_INDEX,DC_RELEASE", "DC." & VectorTable, "", cnt.CounterName & "_DCVECTOR,DC_RELEASE", extendedCountObject, "", "", "")
                    For JoinCount = 0 To UBound(JoinsArray)
                        Dim univ_join = New UnivJoinsTPIde.UnivJoin
                        univ_join.Expression = JoinsArray(JoinCount)
                        univ_join.Cardinality = "n_to_1"
                        univ_join.Contexts = ""
                        AddItem(univ_join)
                    Next JoinCount
                    Else
                        ' Nothing
                        Trace.WriteLine("No range is defined for the type id " & cnt.MeasurementTypeID & " Counter: " & cnt.CounterName & ", not adding join for this vector counter")
                        Console.WriteLine("No range is defined for the type id " & cnt.MeasurementTypeID & " Counter: " & cnt.CounterName & ", not adding join for this vector counter")
                    End If
                    'Check for whether vector counter has range or not.
                End If
            Next cnt_count
        Next count

    End Sub

    ''
    ' Builds measurement lists for measurement types. 
    ' This is called before the joins are set up. Used in CreateJoinExpression.
    '
    ' @param mts Specifies reference to MeasurementTypes
    Public Sub buildLists(ByRef mts As MeasurementTypesTPIde, ByRef extendedCountObject As Boolean)

        Dim mt As MeasurementTypesTPIde.MeasurementType
        Dim mt_count As Integer

        'build measurements for joins
        ReDim Preserve measurements(2000)
        measurements_count = 0
        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)

            If mt.RankTable = False Then
                If mt.PlainTable = False Then
                    measurements(measurements_count) = "DC." & mt.TypeName & "_RAW"
                    measurements_count += 1
                End If
                If mt.PlainTable = True Then
                    measurements(measurements_count) = "DC." & mt.TypeName
                    measurements_count += 1
                End If
                If mt.CreateCountTable = True Then
                    measurements(measurements_count) = "DC." & mt.TypeName & "_COUNT"
                    measurements_count += 1
                    'placeholder for extended COUNT functionality
                    If extendedCountObject = True Then
                        measurements(measurements_count) = "DC." & mt.TypeName & "_DELTA"
                        measurements_count += 1
                    End If
                    'placeholder for extended COUNT functionality
                End If
                If mt.DayAggregation = True Then
                    measurements(measurements_count) = "DC." & mt.TypeName & "_DAY"
                    measurements_count += 1
                End If
                If mt.ObjectBusyHours <> "" Then
                    measurements(measurements_count) = "DC." & mt.TypeName & "_DAYBH"
                    measurements_count += 1
                End If
            Else
                'WI 2.11
                measurements(measurements_count) = "DC." & mt.TypeName & "_RANKBH"
                measurements_count += 1
            End If
        Next mt_count
        ReDim Preserve measurements(measurements_count - 1)

        ReDim Preserve basic_measurements(300)
        basic_measurements_count = 0
        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.RankTable = False Then
                'If mt.PlainTable = True Then
                basic_measurements(basic_measurements_count) = "DC." & mt.TypeName
                basic_measurements_count = basic_measurements_count + 1
                'End If
                'If mt.PlainTable = False Then
                'basic_measurements(basic_measurements_count) = "DC." & mt.MeasurementTypeID & "_RAW"
                'basic_measurements_count = basic_measurements_count + 1
                'End If
            End If
        Next mt_count
        ReDim Preserve basic_measurements(basic_measurements_count - 1)
    End Sub

    ''
    ' Builds join expressions
    '
    ' @param StartTables Defines starting tables
    ' @param StartLevels Defines starting table levels
    ' @param StartColumns Defines starting columns
    ' @param EndTables Defines ending tables
    ' @param EndLevels Defines ending table levels
    ' @param EndColumns Defines ending columns
    ' @return Join expression
    Private Function CreateJoinExpression(ByRef StartTables As String, ByRef StartLevels As String, ByRef StartColumns As String, ByRef EndTables As String, _
                                          ByRef EndLevels As String, ByRef EndColumns As String, ByRef extendedCountObject As Boolean, _
                                          ByVal Cardinality As String, ByVal Contexts As String, ByVal ExcludedContexts As String) As Object
        Dim JoinStartColumns() As String
        Dim JoinEndColumns() As String

        Dim JoinStartTableLevels() As String
        'Dim JoinEndTableLevels() As String

        Dim JoinStartLevels() As String
        Dim JoinStartTables() As String
        Dim col_count As Integer
        Dim meas_count As Integer
        Dim basic_meas_count As Integer
        Dim table_count As Integer
        Dim start_level_count As Integer
        Dim start_table_count As Integer
        Dim JoinExpression As String
        Dim ExpressionStart As String
        Dim ExpressionEnd As String
        ExpressionStart = ""
        ExpressionEnd = ""

        JoinStartColumns = Split(StartColumns, ",")
        JoinEndColumns = Split(EndColumns, ",")

        tpUtilities = New TPUtilitiesTPIde

        ReDim Preserve JoinStartTableLevels(2000)
        start_table_count = 0

        If LCase(StartTables) = "all" AndAlso LCase(StartLevels) <> "all" Then
            JoinStartLevels = Split(StartLevels, ",")
            For start_level_count = 0 To UBound(JoinStartLevels)
                For basic_meas_count = 0 To UBound(basic_measurements)
                    For meas_count = 0 To UBound(measurements)
                        If measurements(meas_count) = basic_measurements(basic_meas_count) & "_" & JoinStartLevels(start_level_count) Then 'have to specify level, only works for basic measurement types
                            JoinStartTableLevels(start_table_count) = measurements(meas_count)
                            start_table_count += 1
                        End If
                    Next meas_count
                Next basic_meas_count
            Next start_level_count

        ElseIf LCase(StartTables) = "all" AndAlso LCase(StartLevels) = "all" Then 'ALL
            For meas_count = 0 To UBound(measurements)
                JoinStartTableLevels(start_table_count) = measurements(meas_count)
                start_table_count += 1
            Next meas_count

        ElseIf LCase(StartTables) <> "all" AndAlso LCase(StartLevels) = "all" Then
            JoinStartTables = Split(StartTables, ",")
            For table_count = 0 To UBound(JoinStartTables)
                For meas_count = 0 To UBound(measurements)
                    If measurements(meas_count) = JoinStartTables(table_count) Then 'Plain table
                        JoinStartTableLevels(start_table_count) = measurements(meas_count)
                        start_table_count += 1
                    End If
                    If measurements(meas_count) = JoinStartTables(table_count) & "_RAW" Then
                        JoinStartTableLevels(start_table_count) = measurements(meas_count)
                        start_table_count += 1
                    End If
                    If measurements(meas_count) = JoinStartTables(table_count) & "_COUNT" Then
                        JoinStartTableLevels(start_table_count) = measurements(meas_count)
                        start_table_count += 1
                    End If
                    'placeholder for extended COUNT functionality
                    If extendedCountObject = True Then
                        If measurements(meas_count) = JoinStartTables(table_count) & "_DELTA" Then
                            JoinStartTableLevels(start_table_count) = measurements(meas_count)
                            start_table_count += 1
                        End If
                    End If
                    'placeholder for extended COUNT functionality
                    If measurements(meas_count) = JoinStartTables(table_count) & "_DAY" Then
                        JoinStartTableLevels(start_table_count) = measurements(meas_count)
                        start_table_count += 1
                    End If
                    If measurements(meas_count) = JoinStartTables(table_count) & "_DAYBH" Then
                        JoinStartTableLevels(start_table_count) = measurements(meas_count)
                        start_table_count += 1
                    End If
                    'WI 2.11
                    If measurements(meas_count) = JoinStartTables(table_count) & "_RANKBH" Then
                        JoinStartTableLevels(start_table_count) = measurements(meas_count) 'have to specify table, not done for RANKBH, so this has no effect
                        start_table_count += 1
                    End If
                Next meas_count
            Next table_count

        Else
            JoinStartTables = Split(StartTables, ",")
            For table_count = 0 To UBound(JoinStartTables)
                If StartLevels = "" Then
                    JoinStartTableLevels(start_table_count) = JoinStartTables(table_count)
                    start_table_count += 1
                Else
                    JoinStartLevels = Split(StartLevels, ",")
                    For start_level_count = 0 To UBound(JoinStartLevels)
                        JoinStartTableLevels(start_table_count) = JoinStartTables(table_count) & "_" & JoinStartLevels(start_level_count) ' have to specify table and level
                        start_table_count += 1
                    Next start_level_count
                End If
            Next table_count
        End If

        ReDim Preserve JoinStartTableLevels(start_table_count)


        ReDim Preserve JoinsArray(2000)
        JoinCount = 0

        For start_table_count = 0 To UBound(JoinStartTableLevels)
            'all measurement and specified levels (1. table only)
            If JoinStartTableLevels(start_table_count) <> "" Then
                JoinExpression = ""

                For col_count = 0 To UBound(JoinStartColumns)
                    If JoinStartColumns(col_count) <> "" Then

                        ExpressionStart = JoinStartTableLevels(start_table_count) & "." & JoinStartColumns(col_count)
                        ' rankBHJoinExpression = ""

                        If EndLevels <> "" Then
                            ExpressionEnd = EndTables & "_" & EndLevels + "." & JoinEndColumns(col_count)
                        Else
                            ExpressionEnd = EndTables & "." & JoinEndColumns(col_count)
                        End If

                        'multiple columns
                        If col_count > 0 Then
                            JoinExpression &= " and " & ExpressionStart & "=" & ExpressionEnd
                        Else
                            JoinExpression = ExpressionStart & "=" & ExpressionEnd
                        End If
                    End If
                Next col_count
                'add join
                JoinsArray(JoinCount) = JoinExpression
                JoinCount += 1

                If (enableRankBusyHourFunctionality = True) Then
                    ' Go through again for rank bh:
                    createRankBHJoinExpression(JoinStartColumns, EndLevels, EndTables, JoinEndColumns, JoinStartTableLevels, start_table_count, Cardinality)
                End If
            End If
        Next start_table_count

        ReDim Preserve JoinsArray(JoinCount - 1)

    End Function

    ''
    ' Creates extra join expression for rank busy hour. Insert into rankBusyHourJoinExpressions ArrayList.
    ' 
    '@param JoinStartColumns
    '@param EndLevels
    '@param EndTables
    '@param JoinEndColumns
    '@param JoinStartTableLevels
    '@param start_table_count
    '@param Cardinality
    Public Sub createRankBHJoinExpression(ByRef JoinStartColumns As String(), ByRef EndLevels As String, ByRef EndTables As String, ByRef JoinEndColumns As String(), _
                                          ByRef JoinStartTableLevels As String(), ByVal start_table_count As Integer, ByVal Cardinality As String)
        Dim col_count As Integer
        Dim ExpressionEnd As String

        Dim rankBHJoinExpression As String
        rankBHJoinExpression = ""
        Dim rankBusyHourJoinExpressions As ArrayList
        rankBusyHourJoinExpressions = New ArrayList()

        ' Go through join start columns for rank bh:
        For col_count = 0 To UBound(JoinStartColumns)
            ' ExpressionStart = JoinStartTableLevels(start_table_count) & "." & JoinStartColumns(col_count)

            If EndLevels <> "" Then
                ExpressionEnd = EndTables & "_" & EndLevels + "." & JoinEndColumns(col_count)
            Else
                ExpressionEnd = EndTables & "." & JoinEndColumns(col_count)
            End If

            ' Calculate the rank bh join expressions from the original expressions:
            Dim rankBHJoins As ArrayList
            rankBHJoins = getRankBHJoinExpressions(JoinStartTableLevels(start_table_count), JoinStartColumns(col_count), ExpressionEnd)

            For joinCount As Integer = 0 To (rankBHJoins.Count - 1) ' e.g. 0 to 2
                Dim newRankJoinPart As String
                newRankJoinPart = rankBHJoins.Item(joinCount)

                If (col_count = 0) Then
                    rankBusyHourJoinExpressions.Insert(joinCount, newRankJoinPart) ' "copy" the array to begin with
                ElseIf (col_count > 0) Then
                    Dim existingJoin As String
                    existingJoin = rankBusyHourJoinExpressions.Item(joinCount) ' get the join we have already

                    ' If the join we have already isn't empty, add " and " to put the joins together:
                    If (existingJoin <> "") Then
                        existingJoin &= " and "
                    End If
                    existingJoin &= newRankJoinPart
                    ' Reinsert existing join:
                    rankBusyHourJoinExpressions.Insert(joinCount, existingJoin)
                End If
            Next
        Next col_count

        ' add extra rank busy hour joins:
        Dim rankBusyHourJoinExpression As String
        For Each rankBusyHourJoinExpression In rankBusyHourJoinExpressions
            addRankBHJoin(rankBusyHourJoinExpression, Cardinality, rankBusyHourJoinExpressions)
        Next
    End Sub

    ''
    '@param startTable
    '@param startColumn
    '@param ExpressionEnd
    '@returns An ArrayList of rank busy hour join expressions.    
    Public Function getRankBHJoinExpressions(ByVal startTable As String, ByVal startColumn As String, ByVal ExpressionEnd As String) As ArrayList

        Dim keys As ICollection = rankBHBaseTypes.Keys
        Dim targetTypes(rankBHBaseTypes.Count - 1) As String
        keys.CopyTo(targetTypes, 0)

        Dim targetType As String
        Dim JoinExpressions As ArrayList
        JoinExpressions = New ArrayList() ' initialise new arrayList
        Dim ExpressionStart As String
        ExpressionStart = ""
        Dim joinIndex As Integer
        joinIndex = 0

        If (ExpressionEnd.Contains("RANKBH") = False) Then ' Don't do this for Rank busy hour tables as end table, only rank tables to topology.
            For Each targetType In targetTypes
                If ((startTable = "DC." & targetType & "_RAW") Or (startTable = "DC." & targetType & "_COUNT")) Then
                    ' If the start table is involved in a busy hour, create the same joins on the keys in the RANKBH table:
                    Dim rankBHTables As ArrayList
                    rankBHTables = rankBHBaseTypes.Item(targetType) ' Get the rankBH tables associated with it. Key because it matches, but only if it does.

                    If Not (rankBHTables Is Nothing) Then
                        ' Same for RANKBH table:
                        Dim rankBHTable As String
                        For Each rankBHTable In rankBHTables 'Go through rankbh tables associated with target type
                            Dim mt As MeasurementTypesTPIde.MeasurementType
                            mt = tpUtilities.getMeasurementTypeByName(rankBHTable, mtypes)

                            If Not (mt Is Nothing) Then
                                Dim rankbhKeys As CounterKeysTPIde
                                rankbhKeys = mt.CounterKeys

                                Dim counterKey As CounterKeysTPIde.CounterKey
                                Dim index As Integer

                                Dim found As Boolean
                                found = False
                                For index = 1 To rankbhKeys.Count 'Go through keys of the rankBH table
                                    counterKey = rankbhKeys.Item(index)
                                    If (counterKey.CounterKeyName = startColumn) Then ' only do this if key same as column in "base" table
                                        found = True
                                        ExpressionStart = "DC." & rankBHTable & "_RANKBH" & "." & startColumn
                                        JoinExpressions.Add(ExpressionStart & "=" & ExpressionEnd)
                                        Exit For
                                    End If
                                Next
                                If (found = False) Then ' not found
                                    JoinExpressions.Add("") ' empty string
                                End If
                            End If
                        Next
                    End If
                End If
            Next
        End If
        Return JoinExpressions
    End Function

    ''
    'Adds a rank busy hour join to the main ArrayList of joins.
    '@param JoinExpression    
    Public Sub addRankBHJoin(ByVal JoinExpression As String, ByVal Cardinality As String, ByVal rankBusyHourJoinExpressions As ArrayList)
        If (listOfRankBHJoinExpressions.Contains(JoinExpression) = False And (JoinExpression <> "")) Then
            listOfRankBHJoinExpressions.Add(JoinExpression)

            Dim rankBusyHourJoin As UnivJoinsTPIde.UnivJoin
            rankBusyHourJoin = New UnivJoinsTPIde.UnivJoin
            rankBusyHourJoin.Expression = JoinExpression
            rankBusyHourJoin.Cardinality = Cardinality 'This stays the same
            rankBusyHourJoin.Contexts = "RANKBH"
            rankBusyHourJoin.ExcludedContexts = ""
            rankBusyHourJoins.Add(rankBusyHourJoin)
        End If
    End Sub

    ''
    ' Gets the target types for a rank busy hour.
    '@returns A Hashtable with the target type as key and an ArrayList of rankBH types associated with it as value.
    Public Function getBHTargetTypes() As Hashtable

        Dim hashtable As Hashtable
        hashtable = New Hashtable

        Dim JoinExpression As String
        JoinExpression = ""

        Dim Count As Integer

        For Count = 0 To (rankMTs.Count - 1)
            ' Get the rank measurement type:
            Dim rankMt As MeasurementTypesTPIde.MeasurementType
            rankMt = rankMTs.Item(Count)

            Dim keys As CounterKeysTPIde
            keys = rankMt.CounterKeys ' 1 based

            Dim targetTypes As ArrayList
            targetTypes = tpUtilities.getBHTargetTypes(techpackVersion, rankMt, tpConn)

            Dim baseType As String
            For Each baseType In targetTypes
                Try
                    If (hashtable.ContainsKey(baseType) = False) Then
                        ' New rank measurement type:
                        Dim newList As ArrayList
                        newList = New ArrayList()
                        newList.Add(rankMt.TypeName)
                        hashtable.Add(baseType, newList)
                    Else
                        ' Contains the base type as key already:
                        Dim list As ArrayList
                        list = hashtable.Item(baseType)
                        list.Add(rankMt.TypeName)
                    End If

                Catch ex As Exception
                    Console.WriteLine("Error getting base types: " & ex.ToString())
                End Try
            Next

        Next
        Return hashtable
    End Function

End Class
