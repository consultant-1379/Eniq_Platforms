Option Strict Off

''
'  UniverseMeasurementsTPIde class is a collection of UniverseMeasurement classes
'
Public Class UniverseMeasurementsTPIde
    Private _items As System.Collections.ArrayList = New System.Collections.ArrayList

    Private techPackName As String
    Private tpConn As System.Data.Odbc.OdbcConnection
    Private baseConn As System.Data.Odbc.OdbcConnection
    Private dbCommand As System.Data.Odbc.OdbcCommand
    Private dbReader As System.Data.Odbc.OdbcDataReader
    Private mts As MeasurementTypesTPIde
    Private TechPackTPIde As String
    Private BaseTechPackTPIde As String
    Private extendedCountObject As String
    Private TPVersion As String
    Private RankBusyHourFunctionality As Boolean

    ' Set up a new UniverseMeasurementsTPIde class:
    Public Sub New(ByVal TechPackName As String, ByVal tpConn As System.Data.Odbc.OdbcConnection, ByRef baseConn As System.Data.Odbc.OdbcConnection, _
                   ByRef dbCommand As System.Data.Odbc.OdbcCommand, ByRef dbReader As System.Data.Odbc.OdbcDataReader, _
                   ByRef mts As MeasurementTypesTPIde, ByVal TechPackTPIde As String, ByVal BaseTechPackTPIde As String, ByVal extendedCountObject As Boolean, _
                   ByVal rankBusyHourFunctionality As Boolean, ByVal TPVersion As String)
        Me.techPackName = TechPackName
        Me.tpConn = tpConn
        Me.baseConn = baseConn
        Me.dbCommand = dbCommand
        Me.dbReader = dbReader
        Me.mts = mts
        Me.TechPackTPIde = TechPackTPIde
        Me.BaseTechPackTPIde = BaseTechPackTPIde
        Me.extendedCountObject = extendedCountObject
        Me.RankBusyHourFunctionality = rankBusyHourFunctionality
        Me.TPVersion = TPVersion
    End Sub

    Public Sub New()

    End Sub

    ''
    '  Gets count of UnivClass classes in UnivClasses class
    '
    ' @param Index Specifies the index in the UnivClasses class
    ' @return Count of UnivClass classes
    Public ReadOnly Property Count() As Integer
        Get
            If (Not _items Is Nothing) Then
                Return _items.Count
            End If
            Return 0
        End Get
    End Property

    ''
    '  Gets UnivClass class from UnivClasses class based on given index.
    '
    ' @param Index Specifies the index in the UnivClasses class
    ' @return Reference to UnivClass
    Public ReadOnly Property Item(ByVal Index As Integer) As UniverseMeasurement
        Get
            If (Index > 0) And (Index <= Me.Count) Then
                Return CType(_items.Item(Index - 1), UniverseMeasurement)
            End If
            Return Nothing
        End Get
    End Property

    ''
    '  Adds UnivClass class to UnivClasses class
    '
    ' @param ValueIn Specifies reference to UnivClass
    Public Sub AddItem(ByVal ValueIn As UniverseMeasurement)

        If (Not _items Is Nothing) Then
            _items.Add(ValueIn)
        End If

    End Sub

    ''
    '  UniverseMeasurement defines a single universe.
    '
    Public Class UniverseMeasurement
        Private m_MeasurementTypes As MeasurementTypesTPIde
        Private m_UniverseExtension As String
        Private m_UniverseNameExtension As String
        Private m_UnivJoins As UnivJoinsTPIde
        Private m_ReferenceTypes As ReferenceTypesTPIde
        Private m_ReferenceDatas As ReferenceDatasTPIde
        Private m_VectorReferenceTypes As ReferenceTypesTPIde
        Private m_VectorReferenceDatas As ReferenceDatasTPIde


        Public Property UniverseExtension()
            Get
                UniverseExtension = m_UniverseExtension
            End Get

            Set(ByVal Value)
                m_UniverseExtension = Value
            End Set

        End Property

        Public Property UniverseNameExtension()
            Get
                UniverseNameExtension = m_UniverseNameExtension
            End Get

            Set(ByVal Value)
                m_UniverseNameExtension = LCase(Value)
            End Set

        End Property

        Public Property MeasurementTypes() As MeasurementTypesTPIde
            Get
                MeasurementTypes = m_MeasurementTypes
            End Get

            Set(ByVal Value As MeasurementTypesTPIde)
                m_MeasurementTypes = Value
            End Set

        End Property

        Public Property UnivJoins() As UnivJoinsTPIde
            Get
                UnivJoins = m_UnivJoins
            End Get

            Set(ByVal Value As UnivJoinsTPIde)
                m_UnivJoins = Value
            End Set

        End Property

        Public Property ReferenceTypes()
            Get
                ReferenceTypes = m_ReferenceTypes
            End Get

            Set(ByVal Value)
                m_ReferenceTypes = Value
            End Set

        End Property

        Public Property ReferenceDatas()
            Get
                ReferenceDatas = m_ReferenceDatas
            End Get

            Set(ByVal Value)
                m_ReferenceDatas = Value
            End Set

        End Property

        Public Property VectorReferenceTypes()
            Get
                VectorReferenceTypes = m_VectorReferenceTypes
            End Get

            Set(ByVal Value)
                m_VectorReferenceTypes = Value
            End Set

        End Property

        Public Property VectorReferenceDatas()
            Get
                VectorReferenceDatas = m_VectorReferenceDatas
            End Get

            Set(ByVal Value)
                m_VectorReferenceDatas = Value
            End Set

        End Property

    End Class

    ''
    'Gets the UniverseMeasurementsTPIde object. This holds all of the UniverseMeasurement objects (each one represents a universe).
    '@param ExtendedUnvList         A list of universe strings, in the format: "a=Standard,b=Extended,c=WRAN,d=TDRAN"
    '@param fullListOfMeasTypes     All of the measurement types.
    '@returns                       A new UniverseMeasurementsTPIde, holding all of the universes (UniverseMeasurement objects)
    Public Sub createUniverseMeasurements(ByVal ExtendedUnvList() As String, ByRef fullListOfMeasTypes As MeasurementTypesTPIde)
        Dim count As Integer
        Dim UniverseNamextension As String
        Dim UniverseExtension As String

        For count = 0 To UBound(ExtendedUnvList)
            UniverseExtension = getUniverseExtension(ExtendedUnvList(count), False)
            UniverseNamextension = getUniverseExtension(ExtendedUnvList(count), True)

            Dim UnvMt As UniverseMeasurement
            UnvMt = createUniverseMeasurement(UniverseExtension, UniverseNamextension, fullListOfMeasTypes)
            AddItem(UnvMt)
        Next count
    End Sub

    ''
    'Gets the universe extension value.
    '@param     extensionString     A string in the format "a=Standard".
    '@param     getName             True if we need the universe letter extension e.g. "a", otherwise returns the extension e.g. "Standard"
    '@returns   extension           The letter extension e.g. "a" if getName is True, otherwise returns the extension e.g. "Standard"
    Public Function getUniverseExtension(ByVal extensionString As String, ByVal getName As Boolean) As String
        Dim extension As String = ""
        Dim UniverseInfo() As String

        If Trim(extensionString) = "" Then
            extension = ""
        Else
            UniverseInfo = extensionString.Split("=")
            If (UniverseInfo.Length = 2) Then
                If (getName = True) Then
                    ' Return the name (currently the letter)
                    extension = Trim(LCase(UniverseInfo(0)))
                Else
                    ' Return the extension
                    extension = Trim(UniverseInfo(1))
                End If
            Else
                ' String was not parsed properly:
                extension = ""
            End If
        End If
        Return extension
    End Function

    ''
    'Creates a single UniverseMeasurement object.
    '@param UniverseExtension       The universe extension (e.g. Standard)
    '@param UniverseNameExtension   The universe extension as a letter (e.g. "a")
    '@param fullListOfMeasTypes     Full list of measurement types.
    '@returns                       A new UniverseMeasurement object.
    Public Function createUniverseMeasurement(ByVal UniverseExtension As String, ByVal UniverseNameExtension As String, _
                                          ByVal fullListOfMeasTypes As MeasurementTypesTPIde) _
                                          As UniverseMeasurementsTPIde.UniverseMeasurement
        ' Set up the UniverseMeasurement object:
        Dim UnvMt As UniverseMeasurementsTPIde.UniverseMeasurement
        UnvMt = New UniverseMeasurementsTPIde.UniverseMeasurement
        Try
            ' List of measurement types for this universe.
            Dim mts As MeasurementTypesTPIde
            mts = getMTsForUniverse(fullListOfMeasTypes, UniverseNameExtension)
            Dim univ_joins As UnivJoinsTPIde = createListOfJoins(mts, UniverseNameExtension)
            Dim rts As ReferenceTypesTPIde = getReferenceTypes(mts)
            Dim rds As ReferenceDatasTPIde = getReferenceDatas(mts)
            Dim vector_rts As ReferenceTypesTPIde = getVectorReferenceTypes(mts)
            Dim vector_rds As ReferenceDatasTPIde = getVectorReferenceDatas(mts)

            UnvMt.UniverseExtension = UniverseExtension
            UnvMt.UniverseNameExtension = UniverseNameExtension
            UnvMt.MeasurementTypes = mts
            UnvMt.ReferenceTypes = rts
            UnvMt.ReferenceDatas = rds
            UnvMt.UnivJoins = univ_joins
            UnvMt.VectorReferenceTypes = vector_rts
            UnvMt.VectorReferenceDatas = vector_rds
        Catch ex As Exception
            Trace.WriteLine("Error creating UniverseMeasurement: " & ex.ToString())
        End Try
        Return UnvMt
    End Function

    ''
    'Gets the reference types for the current universe.
    '@param     mts     The list of measurement types for the current universe.
    '@returns   rts     List of reference types (ReferenceTypesTPIde object).
    Public Overridable Function getReferenceTypes(ByVal mts As MeasurementTypesTPIde) As ReferenceTypesTPIde
        Dim rts = New ReferenceTypesTPIde
        rts.getTopology(techPackName, tpConn, dbCommand, dbReader, mts, TechPackTPIde)
        Return rts
    End Function

    ''
    'Gets the reference datas for the current universe.
    '@param     mts     The list of measurement types for the current universe.
    '@returns   rds     List of reference datas (ReferenceDatasTPIde object).
    Public Overridable Function getReferenceDatas(ByVal mts As MeasurementTypesTPIde) As ReferenceDatasTPIde
        Dim rds = New ReferenceDatasTPIde
        rds.getTopology(techPackName, tpConn, dbCommand, dbReader, mts, Nothing, TechPackTPIde)
        Return rds
    End Function

    ''
    'Gets the vector reference types for the current universe.
    '@param     mts         The list of measurement types for the current universe.
    '@returns   vector_rts  List of reference types (ReferenceTypesTPIde object).
    Public Overridable Function getVectorReferenceTypes(ByVal mts As MeasurementTypesTPIde) As ReferenceTypesTPIde
        Dim vector_rts = New ReferenceTypesTPIde
        vector_rts.getVectorTopology(mts, tpConn)
        Return vector_rts
    End Function

    ''
    'Gets the vector reference datas for the current universe.
    '@param     mts         The list of measurement types for the current universe.
    '@returns   vector_rds  List of reference datas (ReferenceDatasTPIde object).
    Public Overridable Function getVectorReferenceDatas(ByVal mts As MeasurementTypesTPIde) As ReferenceDatasTPIde
        Dim vector_rds = New ReferenceDatasTPIde
        vector_rds.getVectorTopology(mts, tpConn)
        Return vector_rds
    End Function

    ''
    'Creates the list of joins for a universe.
    '@param     measTypes               The list of measurement types for the current universe.
    '@param     UniverseNamextension    The universe name extension for the universe.
    '@returns   univ_joins              A new ArrayList of joins.
    '@remarks   Called once for each universe.
    Public Overridable Function createListOfJoins(ByVal measTypes As MeasurementTypesTPIde, ByVal UniverseNamextension As String) As UnivJoinsTPIde
        Dim univ_joins As UnivJoinsTPIde = New UnivJoinsTPIde()
        univ_joins.buildLists(measTypes, extendedCountObject)

        If univ_joins.getJoins(techPackName, measTypes, baseConn, dbCommand, dbReader, BaseTechPackTPIde, extendedCountObject, TPVersion, False, _
                       UniverseNamextension, RankBusyHourFunctionality) = False Then
            Throw New Exception("Failed to get main tech pack joins")
        End If
        If univ_joins.getJoins(techPackName, measTypes, tpConn, dbCommand, dbReader, TechPackTPIde, extendedCountObject, TPVersion, True, _
                               UniverseNamextension, RankBusyHourFunctionality) = False Then
            Throw New Exception("Failed to get base tech pack joins")
        End If

        univ_joins.getVectorJoins(measTypes, extendedCountObject)

        Return univ_joins
    End Function

    ''
    'Gets the list of measurement types that belong to a given universe.
    '@param fullListOfMTypes        The full list of measurement types for the tech pack, across all universes.
    '@param universeNameExtension   The universe extension(s) (e.g. "a,b,c,d", just "a", "ALL" etc) of the current universe.
    '@returns measTypesForUniverse  The measurement types for the universe.
    '@remarks                       The list is returned and then added to UniverseMeasurementsTPIde.UniverseMeasurement.MeasurementTypes.
    Public Function getMTsForUniverse(ByRef fullListOfMTypes As MeasurementTypesTPIde, _
                                                          ByVal universeNameExtension As String) As MeasurementTypesTPIde
        ' The list of measurement types for this universe
        Dim measTypesForUniverse As MeasurementTypesTPIde = New MeasurementTypesTPIde
        Dim measType As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        Dim measTypeCount As Integer

        Try
            For measTypeCount = 1 To fullListOfMTypes.Count
                ' Get a measurement type:
                measType = fullListOfMTypes.Item(measTypeCount)

                Dim add As Boolean = False
                If measType.ExtendedUniverse = "all" Then
                    add = True
                ElseIf measType.ExtendedUniverse = "" AndAlso universeNameExtension = "" Then
                    add = True
                ElseIf singleExtensionMatches(measType.ExtendedUniverse, universeNameExtension) Or _
                listExtensionMatches(measType.ExtendedUniverse, universeNameExtension) Then
                    add = True
                End If
                If (add = True) Then
                    measTypesForUniverse.AddItem(measType)
                    Trace.WriteLine("Added " & measType.TypeName & " to list for universe " & universeNameExtension)
                End If
            Next measTypeCount
        Catch ex As Exception
            Console.WriteLine("Error getting list of measurement types: " & ex.ToString())
            Trace.WriteLine("Error getting list of measurement types: " & ex.ToString())
        End Try

        Return measTypesForUniverse
    End Function

    ''
    ' Checks if the current universe extension is equal to the universe extension for a measurement type.
    '@param measTypeExtension       The extension of the measurement type.
    '@param universeNameExtension   The universe extension (a single value like "a") of the current universe.
    '@returns                       True if the single value matches.
    Public Function singleExtensionMatches(ByVal measTypeExtension As String, ByVal universeNameExtension As String) As Boolean
        Dim matches As Boolean = False

        ' If the extension value is not a list, compare the values directly:
        If InStrRev(measTypeExtension, ",") = 0 Then
            If measTypeExtension = universeNameExtension Then
                matches = True
            End If
        End If
        Return matches
    End Function

    ''
    ' Checks if the current universe extension is in the list of universe extensions for a measurement type.
    '@param measType                The extension of the measurement type.
    '@param universeNameExtension   The universe extension(s) (a list e.g. "a,b,c,d") of the current universe.
    '@returns isInList              True if the current universe extension is in the list of extensions of the measurement type.
    Public Function listExtensionMatches(ByVal measTypeExtension As String, ByVal universeNameExtension As String) As Boolean

        Dim matches As Boolean = False
        ' The list of universe extensions:
        Dim universeExtensions() As String
        ' Integer to iterate through the universe extensions:
        Dim unvCount As Integer

        ' If the universe extension value is a list separated by "," go through
        ' the list and check if the measurement type's extension matches any 
        ' in the list:
        universeExtensions = Split(measTypeExtension, ",")
        For unvCount = 0 To UBound(universeExtensions)
            If universeExtensions(unvCount) = universeNameExtension Then
                matches = True
            End If
        Next
        Return matches
    End Function

End Class

