Option Strict Off
Option Explicit On 

Public NotInheritable Class MetadataFunctions
    Dim ExcelApp As Excel.Application

    Dim Datatype As String
    Dim Datasize As String
    Dim Datascale As String

    Dim Wksht As Excel.Worksheet
    'Dim Wksht2 As Excel.Worksheet

    Dim PackRel As String
    Dim SourcePack As String
    Dim Rng As Excel.Range
    'Dim Rng2 As Excel.Range

    Dim RowNum As Integer
    'Dim RowNum2 As Integer

    Dim mts As MeasurementTypes
    Dim all_mts As MeasurementTypes
    Dim mt As MeasurementTypes.MeasurementType

    Dim rts As ReferenceTypes
    Dim rt As ReferenceTypes.ReferenceType
    Dim rds As ReferenceDatas
    Dim rd As ReferenceDatas.ReferenceData
    Dim rt_count As Long
    Dim rd_count As Long

    Dim all_bhobjs As BHObjects
    Dim bhobj As BHObjects.BHObject
    Dim all_bh_types As BHTypes
    Dim bh_type As BHTypes.BHType
    Dim bho_count As Long
    Dim bht_count As Long

    Dim bhs As BusyHours
    Dim bh As BusyHours.BusyHour
    Dim bh_count As Long

    Dim all_cnts As Counters
    Dim cnts As Counters
    Dim cnt As Counters.Counter

    Dim all_cnt_keys As CounterKeys
    Dim cnt_keys As CounterKeys
    Dim cnt_key As CounterKeys.CounterKey

    Dim pub_keys As PublicKeys
    Dim pub_key As PublicKeys.PublicKey

    Dim RowCount As Integer
    Dim RowCount2 As Integer
    Dim mt_count As Long
    Dim cnt_count As Long
    Dim cnt_key_count As Long
    Dim pub_key_count As Long

    Dim TPVersion As String

    Dim Version_Information As String

    'Interfaces
    Dim data_fmts As DataFormats
    Dim data_fmt As DataFormats.DataFormat
    Dim data_fmt_count As Long

    Dim topology_fmts As DataFormats
    Dim topology_fmt As DataFormats.DataFormat
    Dim topology_fmt_count As Long

    Dim InterfaceList() As String
    Dim IntfCount As Integer

    Dim IntfTPVersion As String
    Dim IntfSourcePack As String
    Dim IntfPackRel As String

    Dim key_items As DataItems
    Dim key_item As DataItems.DataItem
    Dim key_item_count As Long

    Dim data_items As DataItems
    Dim data_item As DataItems.DataItem
    Dim data_item_count As Long

    Dim topology_items As DataItems
    Dim topology_item As DataItems.DataItem
    Dim topology_item_count As Long

    Dim da As System.Data.OleDb.OleDbDataAdapter
    Dim ds As System.Data.DataSet
    Dim dr As System.Data.DataRow
    Dim dbCommand As System.Data.OleDb.OleDbCommand
    Dim dbReader As System.Data.OleDb.OleDbDataReader

    Dim tpAdoConn As String
    Dim baseAdoConn As String
    Dim intfAdoConn As String

    Dim tpConn As System.Data.OleDb.OleDbConnection
    Dim baseConn As System.Data.OleDb.OleDbConnection
    Dim intfConn As System.Data.OleDb.OleDbConnection


    'Prints MeasurementTypeClass table metadata information.
    '
    'Input parameters: iFileNum - handler for metadata sql file
    'Input parameters: NewProgram - Support for program 5.0.2 metadata
    '
    'Uses MeasurementType class 
    Sub Metadata_Measurements(ByVal iFileNum As Short, ByVal NewProgram As Boolean)

        Dim AddedClass As String
        AddedClass = ""

        PrintLine(iFileNum, "-- Measurements")
        PrintLine(iFileNum, "")

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            PrintLine(iFileNum, "-- " & mt.MeasurementTypeID)

            If InStrRev(AddedClass, mt.MeasurementTypeClass & ",") = 0 Then
                PrintLine(iFileNum, "insert into MeasurementTypeClass (typeclassid, description, versionid) values ('" & PackRel & ":" & mt.MeasurementTypeClass & "','" & mt.MeasurementTypeClassDescription & "','" & PackRel & "');")
                AddedClass &= mt.MeasurementTypeClass & ","
            End If

            mt.PrintMeasurementInformation(iFileNum, PackRel, NewProgram)
        Next mt_count

        PrintLine(iFileNum, "")

    End Sub

    'Prints VendorTag table metadata information.
    '
    'Input parameters: iFileNum - handler for metadata sql file
    '
    'Uses MeasurementType class 
    Sub Metadata_VendorTag(ByVal iFileNum As Short)

        PrintLine(iFileNum, "-- VendorTag")

        For mt_count = 1 To all_mts.Count
            mt = all_mts.Item(mt_count)

            PrintLine(iFileNum, "insert into VendorTag (TYPEID, TAGID, SOURCE, FILEMASK, STATUS) values ('" & PackRel & ":" & mt.MeasurementTypeID & "','" & mt.VendorMeasurement & "','" & SourcePack & "',null,1);")
        Next mt_count

        PrintLine(iFileNum, "commit;")
        PrintLine(iFileNum, "")

    End Sub
    'Prints LOG_MonitoredTypes table information.
    '
    'Input parameters: iFileNum - handler for metadata sql file
    '
    'Uses MeasurementType class 
    Sub Metadata_MonitoredTypes(ByVal iFileNum As Short)

        PrintLine(iFileNum, "-- LOG_MonitoredTypes")

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)

            PrintLine(iFileNum, "insert into LOG_MonitoredTypes (TECHPACK_NAME, TYPENAME, TIMELEVEL, STATUS, MODIFIED, ACTIVATIONDAY) values ('" & SourcePack & "','" & mt.MeasurementTypeID & "','HOUR','ACTIVE',now(),today());")
        Next mt_count

        PrintLine(iFileNum, "commit;")
        PrintLine(iFileNum, "")

    End Sub

    'Prints ReferenceTable and ReferenceColumn metadata information.
    '
    'Input parameters: iFileNum - handler for metadata sql file
    '
    'Uses TopologyData from TP specification
    'Note! Creates HG indexes by default
    Sub Metadata_TopologyData(ByVal iFileNum As Short)

        Dim Colnumber As Integer
        Dim ColIndex As String

        PrintLine(iFileNum, "-- ReferenceTable")

        For rt_count = 1 To rts.Count
            rt = rts.Item(rt_count)
            If rt.Type = "table" Then
                PrintLine(iFileNum, "insert into ReferenceTable (typeid, typename, versionid, description, status, objectid, objectname, update_policy) values ('" & PackRel & ":" & rt.ReferenceTypeID & "','" & rt.ReferenceTypeID & "','" & PackRel & "','" & rt.Description & "',1,'" & PackRel & ":" & rt.ReferenceTypeID & "','" & rt.ReferenceTypeID & "'," & rt.Update & ");")
                If rt.CurrentRequired = True Then
                    PrintLine(iFileNum, "insert into ReferenceTable (typeid, typename, versionid, description, status, objectid, objectname, update_policy) values ('" & PackRel & ":" & rt.ReferenceTypeID & "_CURRENT_DC','" & rt.ReferenceTypeID & "_CURRENT_DC','" & PackRel & "','" & rt.Description & "',1,'" & PackRel & ":" & rt.ReferenceTypeID & "_CURRENT_DC','" & rt.ReferenceTypeID & "_CURRENT_DC',0);")
                End If

                Colnumber = 0
                rds = rt.ReferenceDatas
                For rd_count = 1 To rds.Count
                    rd = rds.Item(rd_count)

                    Colnumber += 1

                    If rd.Datasize > 255 Then
                        ColIndex = ""
                    Else
                        ColIndex = "HG"
                    End If

                    PrintLine(iFileNum, "insert into ReferenceColumn (typeid, colnumber, dataname, datatype, datasize, datascale, uniquevalue, nullable, indexes,uniquekey) values ('" & PackRel & ":" & rt.ReferenceTypeID & "'," & Colnumber & ",'" & rd.ReferenceDataID & "','" & rd.Datatype & "'," & rd.Datasize & "," & rd.Datascale & "," & rd.MaxAmount & "," & rd.Nullable & ",'" & ColIndex & "'," & rd.DupConstraint & ");")

                    If rt.CurrentRequired = True Then
                        PrintLine(iFileNum, "insert into ReferenceColumn (typeid, colnumber, dataname, datatype, datasize, datascale, uniquevalue, nullable, indexes,uniquekey) values ('" & PackRel & ":" & rt.ReferenceTypeID & "_CURRENT_DC'," & Colnumber & ",'" & rd.ReferenceDataID & "','" & rd.Datatype & "'," & rd.Datasize & "," & rd.Datascale & "," & rd.MaxAmount & "," & rd.Nullable & ",'" & ColIndex & "'," & rd.DupConstraint & ");")
                    End If

                Next rd_count
                PrintLine(iFileNum, "")
            End If
        Next rt_count


    End Sub
    Sub FillInterfaceDefinition(ByRef Filename As String, ByRef BaseFilename As String, ByRef IntfFilename As String)

        'ExcelApp = New Excel.Application
        'ExcelApp.Visible = False

        'If ExcelApp Is Nothing Then
        'MsgBox("Couldn't start Excel")
        'Exit Sub
        'End If

        'Dim oldCI As System.Globalization.CultureInfo = System.Threading.Thread.CurrentThread.CurrentCulture
        'System.Threading.Thread.CurrentThread.CurrentCulture = New System.Globalization.CultureInfo("en-US")

        'ExcelApp.Workbooks.Open(Filename)
        'ExcelApp.Workbooks.Open(BaseFilename)
        'ExcelApp.Workbooks.Open(IntfFilename)

        Dim ClsInit As Short

        tpAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & Filename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;IMEX=1"""
        baseAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & BaseFilename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;IMEX=1"""
        intfAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & IntfFilename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;IMEX=1"""

        tpConn = New System.Data.OleDb.OleDbConnection(tpAdoConn)
        baseConn = New System.Data.OleDb.OleDbConnection(baseAdoConn)
        intfConn = New System.Data.OleDb.OleDbConnection(intfAdoConn)

        tpConn.Open()
        baseConn.Open()
        intfConn.Open()

        ClsInit = Initialize_Classes()

        If ClsInit <> 0 Then
            tpConn.Close()
            baseConn.Close()
            intfConn.Close()

            'ExcelApp.Workbooks(3).Close(SaveChanges:=False)
            'ExcelApp.Workbooks(2).Close(SaveChanges:=False)
            'ExcelApp.Workbooks(1).Close(SaveChanges:=False)
            'ExcelApp.Quit()
            'System.Threading.Thread.CurrentThread.CurrentCulture = oldCI
        Else

            'Interface_UpdateGeneral()
            Interface_UpdateMeasurements()
            'Interface_UpdateKeys()
            'Interface_UpdateCounters()
            'Interface_UpdateTopology()

            tpConn.Close()
            baseConn.Close()
            intfConn.Close()
            'ExcelApp.Workbooks(3).Close(SaveChanges:=True)
            'ExcelApp.Workbooks(2).Close(SaveChanges:=False)
            'ExcelApp.Workbooks(1).Close(SaveChanges:=False)
            'ExcelApp.Quit()

            'System.Threading.Thread.CurrentThread.CurrentCulture = oldCI
        End If


    End Sub
    Private Sub Interface_UpdateGeneral()

        Dim ColNum As Integer

        Dim InterfaceTypes As String
        Dim InterfaceList() As String
        Dim IntfCount As Integer


        Wksht = ExcelApp.Workbooks(3).Worksheets("Coversheet")
        Rng = Wksht.Range("A:C")
        Rng.Cells._Default(5, 3).Value = TPVersion
        InterfaceTypes = Rng.Cells._Default(6, 3).Value
        RowNum = 4

        InterfaceList = Split(InterfaceTypes, ",")

        Wksht = ExcelApp.Workbooks(3).Worksheets("Measurements")
        Rng = Wksht.Range("A:Z")
        ColNum = 3
        For IntfCount = 0 To UBound(InterfaceList)
            Rng.Cells._Default(4, ColNum).Value = InterfaceList(IntfCount)
            ColNum += 1
        Next IntfCount

        Wksht = ExcelApp.Workbooks(3).Worksheets("Keys")
        Rng = Wksht.Range("A:Z")
        ColNum = 4
        For IntfCount = 0 To UBound(InterfaceList)
            Rng.Cells._Default(4, ColNum).Value = InterfaceList(IntfCount)
            ColNum += 1
        Next IntfCount

        Wksht = ExcelApp.Workbooks(3).Worksheets("Counters")
        Rng = Wksht.Range("A:Z")
        ColNum = 4
        For IntfCount = 0 To UBound(InterfaceList)
            Rng.Cells._Default(4, ColNum).Value = InterfaceList(IntfCount)
            ColNum += 1
        Next IntfCount

        Wksht = ExcelApp.Workbooks(3).Worksheets("Topology")
        Rng = Wksht.Range("A:Z")
        ColNum = 3
        For IntfCount = 0 To UBound(InterfaceList)
            Rng.Cells._Default(4, ColNum).Value = InterfaceList(IntfCount)
            ColNum += 1
        Next IntfCount

    End Sub
    Private Sub Interface_UpdateMeasurements()

        'Wksht = ExcelApp.Workbooks(3).Worksheets("Measurements")
        'Rng = Wksht.Range("A:C")

        'RowNum = 5

        'For mt_count = 1 To all_mts.Count
        'mt = all_mts.Item(mt_count)

        'If mt.RankTable = False Then
        'Rng.Cells._Default(RowNum, 1).Value = mt.MeasurementTypeID
        'Rng.Cells._Default(RowNum, 2).Value = mt.Release
        'RowNum += 1
        'End If

        'Next mt_count

        'da = New System.Data.OleDb.OleDbDataAdapter("Select * From [Measurements$A4:B60000]", intfConn)
        'ds = New System.Data.DataSet
        'da.Fill(ds, "Measurements")

        ' Generate the InsertCommand and add the parameters for the command.
        'da.InsertCommand = New System.Data.OleDb.OleDbCommand("INSERT INTO [Measurements$A4:B60000] (MeasurementType, Release) VALUES (?, ?)", intfConn)
        'da.InsertCommand.Parameters.Add("@MeasurementType", System.Data.OleDb.OleDbType.VarChar, 255, "MeasurementType")
        'da.InsertCommand.Parameters.Add("@Release", System.Data.OleDb.OleDbType.VarChar).SourceColumn = "Release"

        ' Add two new records to the dataset.
        'dr = ds.Tables(0).NewRow
        'dr("MeasurementType") = "Foo" : dr("Release") = "Bar" : ds.Tables(0).Rows.Add(dr)

        ' Apply the dataset changes to the actual data source (the workbook).
        'da.Update(ds, "Measurements")

        'Add two records to the table.
        'Dim objCmd As New System.Data.OleDb.OleDbCommand
        'objCmd.Connection = intfConn
        'objCmd.CommandText = "Insert into [Measurements$] (MeasurementType, Release)" & "values ('Bill', 'Brown')"
        'objCmd.ExecuteNonQuery()
        'objCmd.CommandText = "Insert into [Measurements$] (MeasurementType, Release)" & " values ('Joe', 'Thomas')"
        'objCmd.ExecuteNonQuery()



    End Sub
    Private Sub Interface_UpdateKeys()

        Wksht = ExcelApp.Workbooks(3).Worksheets("Keys")
        Rng = Wksht.Range("A:C")

        RowNum = 5


        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)

            If mt.RankTable = False Then

                cnt_keys = mt.CounterKeys
                For cnt_key_count = 1 To cnt_keys.Count

                    cnt_key = cnt_keys.Item(cnt_key_count)

                    Rng.Cells._Default(RowNum, 1).Value = cnt_key.MeasurementTypeID
                    Rng.Cells._Default(RowNum, 2).Value = cnt_key.CounterKeyName
                    Rng.Cells._Default(RowNum, 3).Value = cnt_key.Release
                    RowNum += 1

                Next cnt_key_count

            End If

        Next mt_count

    End Sub
    Private Sub Interface_UpdateCounters()

        Wksht = ExcelApp.Workbooks(3).Worksheets("Counters")
        Rng = Wksht.Range("A:C")

        RowNum = 5

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)

            If mt.RankTable = False Then

                cnts = mt.Counters
                For cnt_count = 1 To cnts.Count
                    cnt = cnts.Item(cnt_count)

                    Rng.Cells._Default(RowNum, 1).Value = cnt.MeasurementTypeID
                    Rng.Cells._Default(RowNum, 2).Value = cnt.CounterName
                    Rng.Cells._Default(RowNum, 3).Value = cnt.Release
                    RowNum += 1

                Next cnt_count

            End If

        Next mt_count

    End Sub
    Private Sub Interface_UpdateTopology()

        Wksht = ExcelApp.Workbooks(3).Worksheets("Topology")
        Rng = Wksht.Range("A:C")

        RowNum = 5

        For rt_count = 1 To rts.Count
            rt = rts.Item(rt_count)
            If rt.Type = "table" Then

                rds = rt.ReferenceDatas
                For rd_count = 1 To rds.Count
                    rd = rds.Item(rd_count)

                    Rng.Cells._Default(RowNum, 1).Value = rt.ReferenceTypeID
                    Rng.Cells._Default(RowNum, 2).Value = rd.ReferenceDataID
                    RowNum += 1

                Next rd_count
            End If
        Next rt_count

    End Sub
    Sub Interface_DefaultTags(ByVal iFileNum As Short)

        Dim TagList() As String
        Dim Inx As Integer

        PrintLine(iFileNum, "-- DefaultTags")


        For data_fmt_count = 1 To data_fmts.Count
            data_fmt = data_fmts.Item(data_fmt_count)
            TagList = Split(data_fmt.TagId, ",")
            For Inx = 0 To UBound(TagList)
                If TagList(Inx) <> "" Then
                    PrintLine(iFileNum, "insert into DefaultTags (tagid, dataformatid, description) values ('" & TagList(Inx) & "','" & data_fmt.DataFormatId & "','" & data_fmt.Description & "');")
                End If
            Next Inx

        Next data_fmt_count

        For topology_fmt_count = 1 To topology_fmts.Count
            topology_fmt = topology_fmts.Item(topology_fmt_count)
            PrintLine(iFileNum, "insert into DefaultTags (tagid, dataformatid, description) values ('" & topology_fmt.TagId & "','" & topology_fmt.DataFormatId & "','" & topology_fmt.Description & "');")
        Next topology_fmt_count

        PrintLine(iFileNum, "")

    End Sub
    Sub Interface_DataFormat(ByVal iFileNum As Short)

        Dim PreviousDataFormatId As String

        PrintLine(iFileNum, "-- DataFormat")

        PreviousDataFormatId = ""
        For data_fmt_count = 1 To data_fmts.Count
            data_fmt = data_fmts.Item(data_fmt_count)
            If data_fmt.DataFormatId <> PreviousDataFormatId Then
                PrintLine(iFileNum, "insert into DataFormat ( dataformatid, typeid, versionid, objecttype, foldername, dataformattype) values ('" & _
                data_fmt.DataFormatId & "','" & _
                data_fmt.TypeId & "','" & _
                data_fmt.VersionId & "','" & _
                "Measurement','" & _
                data_fmt.FolderName & "','" & _
                data_fmt.DataFormatType & "');")
            End If
            PreviousDataFormatId = data_fmt.DataFormatId
        Next data_fmt_count

        PreviousDataFormatId = ""
        For topology_fmt_count = 1 To topology_fmts.Count
            topology_fmt = topology_fmts.Item(topology_fmt_count)
            If topology_fmt.DataFormatId <> PreviousDataFormatId Then
                PrintLine(iFileNum, "insert into DataFormat ( dataformatid, typeid, versionid, objecttype, foldername,dataformattype) values ('" & _
                topology_fmt.DataFormatId & "','" & _
                topology_fmt.TypeId & "','" & _
                topology_fmt.VersionId & "','" & _
                "Reference','" & _
                topology_fmt.FolderName & "','" & _
                topology_fmt.DataFormatType & "');")
            End If
            PreviousDataFormatId = topology_fmt.DataFormatId
        Next topology_fmt_count


        PrintLine(iFileNum, "")

    End Sub
    Sub Interface_DataItem(ByVal iFileNum As Short)

        Dim Colnumber As Long
        Dim KeyCounts As ReleaseCounts
        Dim KeyCount As ReleaseCounts.ReleaseCount
        Dim PreviousDataFormatId As String

        Dim rel_key_cnt As Integer
        Dim RelFound As Boolean

        PrintLine(iFileNum, "-- DataItem")

        For data_fmt_count = 1 To data_fmts.Count
            data_fmt = data_fmts.Item(data_fmt_count)
            If data_fmt.DataFormatId <> PreviousDataFormatId Then


                'Keys
                KeyCounts = New ReleaseCounts
                For key_item_count = 1 To key_items.Count
                    key_item = key_items.Item(key_item_count)

                    If key_item.DataFormatId = data_fmt.DataFormatId Then

                        RelFound = False
                        For rel_key_cnt = 1 To KeyCounts.Count
                            KeyCount = KeyCounts.Item(rel_key_cnt)
                            If KeyCount.Release = key_item.Release Then
                                RelFound = True
                                Exit For
                            End If
                        Next rel_key_cnt
                        If RelFound = False Then
                            KeyCount = New ReleaseCounts.ReleaseCount
                            KeyCount.SetSeed(key_item.Release, 1, "00")
                            KeyCounts.AddItem(KeyCount)
                        End If

                        Colnumber = KeyCount.ColCount
                        PrintLine(iFileNum, "insert into DataItem (dataformatid, dataname, colnumber, dataid) values ('" & key_item.DataFormatId & "','" & key_item.DataName & "'," & Colnumber & ",'" & key_item.DataId & "');")

                    End If
                Next key_item_count

                'Public Keys
                KeyCount = New ReleaseCounts.ReleaseCount
                KeyCount.SetSeed(data_fmt.Release, 50, "0")

                Colnumber = KeyCount.ColCount

                For pub_key_count = 1 To pub_keys.Count
                    pub_key = pub_keys.Item(pub_key_count)
                    If pub_key.KeyType = "RAW" Then
                        Colnumber = KeyCount.ColCount
                        PrintLine(iFileNum, "insert into DataItem (dataformatid, dataname, colnumber, dataid) values ('" & data_fmt.DataFormatId & "','" & pub_key.PublicKeyName & "'," & Colnumber & ",'" & pub_key.Source & "');")
                    End If
                Next pub_key_count



                'Counters
                KeyCounts = New ReleaseCounts
                For data_item_count = 1 To data_items.Count
                    data_item = data_items.Item(data_item_count)

                    If data_item.DataFormatId = data_fmt.DataFormatId Then

                        RelFound = False
                        For rel_key_cnt = 1 To KeyCounts.Count
                            KeyCount = KeyCounts.Item(rel_key_cnt)
                            If KeyCount.Release = data_item.Release Then
                                RelFound = True
                                Exit For
                            End If
                        Next rel_key_cnt
                        If RelFound = False Then
                            KeyCount = New ReleaseCounts.ReleaseCount
                            KeyCount.SetSeed(data_item.Release, 100, "")
                            KeyCounts.AddItem(KeyCount)
                        End If

                        Colnumber = KeyCount.ColCount
                        PrintLine(iFileNum, "insert into DataItem (dataformatid, dataname, colnumber, dataid) values ('" & data_item.DataFormatId & "','" & data_item.DataName & "'," & Colnumber & ",'" & data_item.DataId & "');")

                    End If
                Next data_item_count


            End If
            PreviousDataFormatId = data_fmt.DataFormatId
        Next data_fmt_count

        PreviousDataFormatId = ""
        For topology_fmt_count = 1 To topology_fmts.Count
            topology_fmt = topology_fmts.Item(topology_fmt_count)
            If topology_fmt.DataFormatId <> PreviousDataFormatId Then

                'Counters
                Colnumber = 0
                For topology_item_count = 1 To topology_items.Count
                    topology_item = topology_items.Item(topology_item_count)

                    If topology_item.DataFormatId = topology_fmt.DataFormatId Then

                        Colnumber += 1
                        PrintLine(iFileNum, "insert into DataItem (dataformatid, dataname, colnumber, dataid) values ('" & topology_item.DataFormatId & "','" & topology_item.DataName & "'," & Colnumber & ",'" & topology_item.DataId & "');")

                    End If
                Next topology_item_count
            End If
            PreviousDataFormatId = topology_fmt.DataFormatId
        Next topology_fmt_count

        PrintLine(iFileNum, "")

    End Sub
    Sub MakeMetadata(ByRef Filename As String, ByRef BaseFilename As String, ByRef IntfFilename As String, ByVal V4Compatibility As Boolean, ByRef OutputDir As String, ByVal CreateSample As Boolean, ByVal NewProgram As Boolean)

        'ExcelApp = New Excel.Application
        'ExcelApp.Visible = False

        'If ExcelApp Is Nothing Then
        'MsgBox("Couldn't start Excel")
        'Exit Sub
        'End If

        'Dim oldCI As System.Globalization.CultureInfo = System.Threading.Thread.CurrentThread.CurrentCulture
        'System.Threading.Thread.CurrentThread.CurrentCulture = New System.Globalization.CultureInfo("en-US")


        'ExcelApp.Workbooks.Open(Filename)
        'ExcelApp.Workbooks.Open(BaseFilename)
        'ExcelApp.Workbooks.Open(IntfFilename)



        Dim metafile As String
        'Dim idotpos As Short
        'Dim islashpos As Short
        Dim iFileNum As Short
        '
        tpAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & Filename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;IMEX=1"""
        baseAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & BaseFilename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;IMEX=1"""
        intfAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & IntfFilename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;IMEX=1"""

        tpConn = New System.Data.OleDb.OleDbConnection(tpAdoConn)
        baseConn = New System.Data.OleDb.OleDbConnection(baseAdoConn)
        intfConn = New System.Data.OleDb.OleDbConnection(intfAdoConn)

        tpConn.Open()
        baseConn.Open()
        intfConn.Open()


        Dim ClsInit As Short

        ClsInit = Initialize_Classes()

        If ClsInit <> 0 Then
            tpConn.Close()
            baseConn.Close()
            intfConn.Close()
            'ExcelApp.Workbooks(3).Close(SaveChanges:=False)
            'ExcelApp.Workbooks(2).Close(SaveChanges:=False)
            'ExcelApp.Workbooks(1).Close(SaveChanges:=False)
            'ExcelApp.Quit()

            'System.Threading.Thread.CurrentThread.CurrentCulture = oldCI
        Else

            If CreateSample = True Then
                TechPack_SampleData(OutputDir & "\")
            End If

            'idotpos = InStr(Filename, ".")
            'islashpos = InStrRev(Filename, "\")

            metafile = OutputDir & "\Tech_Pack_" & SourcePack & "_dwh.sql"
            iFileNum = FreeFile()
            FileOpen(iFileNum, metafile, OpenMode.Output)
            'views
            Call TechPack_ExtraViews(iFileNum)
            Call TechPack_TopologyViews(iFileNum)
            Call TechPack_BusyHourViews(iFileNum)
            'procedures
            Call TechPack_ExtraProcedures(iFileNum)
            'inserts
            Call TechPack_ExtraInserts(iFileNum)
            Call TechPack_BusyHourInserts(iFileNum)
            'monitoring
            Call Metadata_MonitoredTypes(iFileNum)
            FileClose(iFileNum)

            metafile = OutputDir & "\Tech_Pack_" & SourcePack & "_metadata.sql"
            iFileNum = FreeFile()
            FileOpen(iFileNum, metafile, OpenMode.Output)

            PrintLine(iFileNum, Version_Information)

            Call Metadata_Measurements(iFileNum, NewProgram)

            Call Metadata_TopologyData(iFileNum)

            Call Metadata_Aggregation(iFileNum)
            Call Metadata_AggregationRules(iFileNum)

            If V4Compatibility = True Then
                Call Metadata_V4Compatibility(iFileNum)
            End If

            Initialize_InterfaceClasses()

            If NewProgram = True Then
                Call Interface_DataFormat(iFileNum)
                Call Interface_DefaultTags(iFileNum)
                Call Interface_DataItem(iFileNum)
            End If

            Call TechPack_ExtraMetadataInserts(iFileNum)

            'Call Metadata_VendorTag(iFileNum)

            FileClose(iFileNum)


            tpConn.Close()
            baseConn.Close()
            intfConn.Close()
            'FileClose(iFileNum)

            'ExcelApp.Workbooks(3).Close(SaveChanges:=False)
            'ExcelApp.Workbooks(2).Close(SaveChanges:=False)
            'ExcelApp.Workbooks(1).Close(SaveChanges:=False)
            'ExcelApp.Quit()

            'System.Threading.Thread.CurrentThread.CurrentCulture = oldCI


        End If

    End Sub
    Private Sub getDatatype(ByRef Value As String)

        Dim TempField3 As Integer
        Dim TempField2 As String
        Dim TempField1 As String
        Dim ReplaceArg As Double
        'Datatype
        If InStrRev(UCase(Value), "UNSIGNED INT") > 0 Then
            Datatype = "unsigned int"
        ElseIf InStrRev(UCase(Value), "SMALLINT") > 0 Then
            Datatype = "smallint"
        ElseIf InStrRev(UCase(Value), "TINYINT") > 0 Then
            Datatype = "tinyint"
        ElseIf InStrRev(UCase(Value), "VARCHAR2") > 0 Then
            Datatype = "varchar2"
        ElseIf InStrRev(UCase(Value), "DOUBLE") > 0 Then
            Datatype = "double"
        ElseIf InStrRev(UCase(Value), "INT") > 0 Then
            Datatype = "int"
        ElseIf InStrRev(UCase(Value), "DATETIME") > 0 Then
            Datatype = "datetime"
        ElseIf InStrRev(UCase(Value), "DATE") > 0 Then
            Datatype = "date"
        ElseIf InStrRev(UCase(Value), "VARCHAR") > 0 Then
            Datatype = "varchar"
        ElseIf InStrRev(UCase(Value), "CHAR") > 0 Then
            Datatype = "char"
        ElseIf InStrRev(UCase(Value), "FLOAT") > 0 Then
            Datatype = "float"
        ElseIf InStrRev(UCase(Value), "LONG") > 0 Then
            Datatype = "long"
        ElseIf InStrRev(UCase(Value), "NUMERIC") > 0 Then
            Datatype = "numeric"
        Else
            Datatype = "NOT FOUND"
        End If

        'Temp Fields
        TempField3 = InStrRev(Value, "(")
        If TempField3 <= 0 Then
            TempField3 = 9999999
            TempField2 = "0"
            TempField1 = "0"
        Else
            TempField2 = Value.Substring(TempField3)
            ReplaceArg = InStrRev(TempField2, ")")
            If ReplaceArg <= 0 Then
                ReplaceArg = 9999999
            End If
            TempField1 = Replace(TempField2, ")", "")
        End If

        'Datasize
        If InStrRev(Datatype, "int") > 0 Then
            Datasize = "0"
        ElseIf InStrRev(Datatype, "date") > 0 Then
            Datasize = "0"
        ElseIf InStrRev(Datatype, "double") > 0 Then
            Datasize = "0"
        ElseIf InStrRev(UCase(Datatype), "NUMERIC") > 0 Then
            If InStrRev(TempField1, ",") > 0 Then
                Datasize = Left(TempField1, InStrRev(TempField1, ",") - 1)
            Else
                If TempField1 = "0" Then
                    Datasize = "Err"
                Else
                    Datasize = TempField1
                End If
            End If

        ElseIf InStrRev(UCase(Datatype), "CHAR") > 0 Then
            If TempField1 = "0" Then
                Datasize = "Err"
            Else
                Datasize = TempField1
            End If
        Else
            Datasize = "0"
        End If
        'Datascale
        If InStrRev(UCase(Datatype), "FLOAT") > 0 Then
            Datascale = "0"
        ElseIf InStrRev(UCase(Datatype), "DOUBLE") > 0 Then
            Datascale = "0"
        ElseIf InStrRev(UCase(Datatype), "NUMERIC") > 0 Then
            If InStrRev(TempField1, ",") > 0 Then
                Datascale = Right(TempField1, Len(TempField1) - InStrRev(TempField1, ","))
            Else
                Datascale = "0"
            End If
        Else
            Datascale = "0"
        End If

    End Sub

    'Function creates clauses for inserting AggregationRule information to DWH repository
    'Input parameters: iFileNum - handler for metadata sql file
    '
    'Uses Coversheet, MeasurementTypes from TP specification
    Sub Metadata_AggregationRules(ByVal iFileNum As Short)

        Dim RuleID As Integer

        Dim RankList() As String
        Dim mts2 As MeasurementTypes
        Dim mt2 As MeasurementTypes.MeasurementType
        Dim mt_count2 As Integer
        Dim Inx As Integer
        Dim Added As Boolean

        PrintLine(iFileNum, "-- AggregationRule")

        'AGGREGATIONRULES
        RuleID = 1

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)

            If mt.RankTable = False Then
                'Day-Raw aggregations
                If mt.CountTable = False AndAlso mt.DayAggregation = True Then
                    PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAY", "RAW", mt.MeasurementTypeID, mt.MeasurementTypeID, "TOTAL", "DAY"))
                    RuleID += 1
                End If

                'Day-Count aggregations
                If mt.CountTable = True AndAlso mt.DayAggregation = True Then
                    PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAY", "COUNT", mt.MeasurementTypeID, mt.MeasurementTypeID, "TOTAL", "DAY"))
                    RuleID += 1
                End If

                'DayBH-Raw aggregations (BHSRC)
                If mt.CountTable = False AndAlso mt.ObjectBusyHours <> "" Then
                    PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "RAW", mt.MeasurementTypeID, mt.MeasurementTypeID, "BHSRC", "DAY"))
                    RuleID += 1
                End If

                'DayBH-Count aggregations (BHSRC)
                If mt.CountTable = True AndAlso mt.ObjectBusyHours <> "" Then
                    PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "COUNT", mt.MeasurementTypeID, mt.MeasurementTypeID, "BHSRC", "DAY"))
                    RuleID += 1
                End If
            End If

            If mt.RankTypes <> "" Then
                'RankBH-Raw aggregation
                If mt.CountTable = False Then
                    'build rank list
                    RankList = Split(mt.RankTypes, ",")
                    mts2 = mts
                    For mt_count2 = 1 To mts2.Count
                        mt2 = mts2.Item(mt_count2)
                        If mt2.RankTable = True Then
                            For Inx = 0 To UBound(RankList)
                                If RankList(Inx) <> "" Then
                                    If InStrRev(mt2.ObjectBusyHours, RankList(Inx)) > 0 OrElse InStrRev(mt2.ElementBusyHours, RankList(Inx)) > 0 Then
                                        PrintLine(iFileNum, PrintRankAggregationRule(RuleID, RankList(Inx), "RANKBH", "RAW", mt2.MeasurementTypeID, mt.MeasurementTypeID, "RANKBH", "DAY"))
                                        RuleID += 1
                                    End If
                                End If
                            Next Inx
                        End If
                    Next mt_count2
                End If

                'RankBH-Count aggregation
                If mt.CountTable = True Then
                    'build rank list
                    RankList = Split(mt.RankTypes, ",")
                    mts2 = mts
                    For mt_count2 = 1 To mts2.Count
                        mt2 = mts2.Item(mt_count2)
                        If mt2.RankTable = True Then
                            For Inx = 0 To UBound(RankList)
                                If RankList(Inx) <> "" Then
                                    If InStrRev(mt2.ObjectBusyHours, RankList(Inx)) > 0 OrElse InStrRev(mt2.ElementBusyHours, RankList(Inx)) > 0 Then
                                        PrintLine(iFileNum, PrintRankAggregationRule(RuleID, RankList(Inx), "RANKBH", "COUNT", mt2.MeasurementTypeID, mt.MeasurementTypeID, "RANKBH", "DAY"))
                                        RuleID += 1
                                    End If
                                End If
                            Next Inx
                        End If
                    Next mt_count2
                End If
            End If

            'DayBH-RankBH aggregation (RANKSRC)
            If mt.ObjectBusyHours <> "" AndAlso mt.RankTable = True Then
                'build rank list
                RankList = Split(mt.ObjectBusyHours, ",")
                mts2 = mts
                For mt_count2 = 1 To mts2.Count
                    mt2 = mts2.Item(mt_count2)
                    If mt2.ObjectBusyHours <> "" AndAlso mt2.RankTable = False Then
                        Added = False
                        For Inx = 0 To UBound(RankList)
                            If RankList(Inx) <> "" Then
                                If InStrRev(mt2.ObjectBusyHours, RankList(Inx)) > 0 AndAlso Added = False Then
                                    PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "RANKBH", mt2.MeasurementTypeID, mt.MeasurementTypeID, "RANKSRC", "DAY"))
                                    RuleID += 1
                                    Added = True
                                End If
                            End If
                        Next Inx
                    End If
                Next mt_count2
            End If

            'Week/MonthRankBH-RankBH aggregation (RANKBHCLASS)
            If mt.RankTable = True Then
                RankList = Split(mt.ObjectBusyHours, ",")
                For Inx = 0 To UBound(RankList)
                    If RankList(Inx) <> "" Then
                        PrintLine(iFileNum, PrintRankAggregationRule(RuleID, RankList(Inx), "RANKBH", "RANKBH", mt.MeasurementTypeID, mt.MeasurementTypeID, "RANKBHCLASS", "WEEK"))
                        RuleID += 1
                        PrintLine(iFileNum, PrintRankAggregationRule(RuleID, RankList(Inx), "RANKBH", "RANKBH", mt.MeasurementTypeID, mt.MeasurementTypeID, "RANKBHCLASS", "MONTH"))
                        RuleID += 1
                    End If
                Next Inx

                RankList = Split(mt.ElementBusyHours, ",")
                For Inx = 0 To UBound(RankList)
                    If RankList(Inx) <> "" Then
                        PrintLine(iFileNum, PrintRankAggregationRule(RuleID, RankList(Inx), "RANKBH", "RANKBH", mt.MeasurementTypeID, mt.MeasurementTypeID, "RANKBHCLASS", "WEEK"))
                        RuleID += 1
                        PrintLine(iFileNum, PrintRankAggregationRule(RuleID, RankList(Inx), "RANKBH", "RANKBH", mt.MeasurementTypeID, mt.MeasurementTypeID, "RANKBHCLASS", "MONTH"))
                        RuleID += 1
                    End If
                Next Inx
            End If


            'Week/MonthBH-RankBH aggregation (DAYBHCLASS)
            If mt.RankTable = True AndAlso mt.ObjectBusyHours <> "" Then
                'build rank list
                RankList = Split(mt.ObjectBusyHours, ",")
                mts2 = mts
                For mt_count2 = 1 To mts2.Count
                    mt2 = mts2.Item(mt_count2)
                    If mt2.ObjectBusyHours <> "" AndAlso mt2.RankTable = False Then
                        Added = False
                        For Inx = 0 To UBound(RankList)
                            If RankList(Inx) <> "" Then
                                If InStrRev(mt2.ObjectBusyHours, RankList(Inx)) > 0 AndAlso Added = False Then
                                    PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "RANKBH", mt2.MeasurementTypeID, mt.MeasurementTypeID, "DAYBHCLASS", "WEEK"))
                                    RuleID += 1
                                    PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "RANKBH", mt2.MeasurementTypeID, mt.MeasurementTypeID, "DAYBHCLASS", "MONTH"))
                                    RuleID += 1
                                    Added = True
                                End If
                            End If
                        Next Inx
                    End If
                Next mt_count2
            End If

            'Week/MonthBH-DayBH aggregation (DAYBHCLASS)
            'If mt.RankTable = False And mt.ObjectBusyHours <> "" Then
            'PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "DAYBH", mt.MeasurementTypeID, mt.MeasurementTypeID, "DAYBHCLASS", "WEEK"))
            'RuleID = RuleID + 1
            'PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "DAYBH", mt.MeasurementTypeID, mt.MeasurementTypeID, "DAYBHCLASS", "MONTH"))
            'RuleID = RuleID + 1
            'End If

            'Raw-Raw aggregations
            If mt.RelatedMeasurements <> "" Then
                'build rank list
                RankList = Split(mt.RelatedMeasurements, ",")

                For Inx = 0 To UBound(RankList)
                    If RankList(Inx) <> "" Then

                        Dim GetVendor() As String
                        Dim CalcVendor As String
                        GetVendor = Split(RankList(Inx), "_")
                        CalcVendor = GetVendor(0) & "_" & GetVendor(1) & "_" & GetVendor(2)

                        PrintLine(iFileNum, PrintViewAggregationRule(RuleID, CalcVendor, "RAW", "RAW", mt.MeasurementTypeID, RankList(Inx), "TOTAL", "DAY"))
                        RuleID += 1
                    End If
                Next Inx
            End If

            'Count-Raw aggregations
            If mt.CountTable = True Then
                PrintLine(iFileNum, PrintAggregationRule(RuleID, "COUNT", "RAW", mt.MeasurementTypeID, mt.MeasurementTypeID, "COUNT", "DAY"))
                RuleID += 1
            End If
        Next mt_count

        PrintLine(iFileNum, "")

    End Sub
    'Function creates clauses for inserting Aggregation information to DWH repository
    'Input parameters: iFileNum - handler for metadata sql file
    '
    'Uses Coversheet, MeasurementTypes from TP specification
    Sub Metadata_Aggregation(ByVal iFileNum As Short)

        Dim RankList() As String
        Dim Inx As Integer

        PrintLine(iFileNum, "-- Aggregation")

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.RankTable = False Then
                If mt.DayAggregation = True Then
                    If mt.CountTable = True Then
                        PrintLine(iFileNum, "insert into Aggregation values('" & mt.MeasurementTypeID & "_COUNT','" & PackRel & "','s_" & mt.MeasurementTypeID & "_COUNT','b_" & PackRel & "_DAY','s_" & PackRel & "_COUNT_REAGG','b_" & PackRel & "_DAY_REAGG'," & "1,3," & "'TOTAL','COUNT');")
                    End If
                    PrintLine(iFileNum, "insert into Aggregation values('" & mt.MeasurementTypeID & "_DAY','" & PackRel & "','s_" & mt.MeasurementTypeID & "_DAY','b_" & PackRel & "_DAY','s_" & mt.MeasurementTypeID & "_DAY_REAGG','b_" & PackRel & "_DAY_REAGG'," & "1,3," & "'TOTAL','DAY');")
                End If
                If mt.ObjectBusyHours <> "" Then
                    PrintLine(iFileNum, "insert into Aggregation values('" & mt.MeasurementTypeID & "_DAYBH','" & PackRel & "','s_" & mt.MeasurementTypeID & "_DAYBH','b_" & PackRel & "_DAY','s_" & mt.MeasurementTypeID & "_DAYBH_REAGG','b_" & PackRel & "_DAY_REAGG'," & "1,4," & "'DAYBH','DAY');")
                    RankList = Split(mt.ObjectBusyHours, ",")
                    For Inx = 0 To UBound(RankList)
                        If RankList(Inx) <> "" Then
                            If InStrRev(mt.ObjectBusyHours, RankList(Inx)) > 0 Then
                                PrintLine(iFileNum, "insert into Aggregation values('" & mt.MeasurementTypeID & "_WEEKBH','" & PackRel & "','s_" & mt.MeasurementTypeID & "_WEEKBH','b_" & PackRel & "_WEEK','s_" & mt.MeasurementTypeID & "_WEEKBH_REAGG','b_" & PackRel & "_WEEK_REAGG'," & "1,3," & "'DAYBH','WEEK');")
                                PrintLine(iFileNum, "insert into Aggregation values('" & mt.MeasurementTypeID & "_MONTHBH','" & PackRel & "','s_" & mt.MeasurementTypeID & "_MONTHBH','b_" & PackRel & "_MONTH','s_" & mt.MeasurementTypeID & "_MONTHBH_REAGG','b_" & PackRel & "_MONTH_REAGG'," & "1,3," & "'DAYBH','MONTH');")
                                Exit For
                            End If
                        End If
                    Next Inx
                End If
                If mt.RelatedMeasurements <> "" Then
                    PrintLine(iFileNum, "insert into Aggregation values('" & mt.MeasurementTypeID & "_RAW','" & PackRel & "','s_" & mt.MeasurementTypeID & "_RAW','b_" & PackRel & "_RAW','s_" & mt.MeasurementTypeID & "_RAW_REAGG','b_" & PackRel & "_RAW_REAGG'," & "1,0," & "'TOTAL','RAW');")
                End If
            End If
            If mt.RankTable = True AndAlso mt.ObjectBusyHours <> "" Then
                RankList = Split(mt.ObjectBusyHours, ",")
                For Inx = 0 To UBound(RankList)
                    If RankList(Inx) <> "" Then
                        PrintLine(iFileNum, "insert into Aggregation values('" & mt.MeasurementTypeID & "_RANKBH_" & RankList(Inx) & "','" & PackRel & "','s_" & mt.MeasurementTypeID & "_RANKBH_" & RankList(Inx) & "','b_" & PackRel & "_DAY','s_" & mt.MeasurementTypeID & "_RANKBH_" & RankList(Inx) & "_REAGG','b_" & PackRel & "_DAY_REAGG'," & "1,1," & "'RANKBH','DAY');")
                        PrintLine(iFileNum, "insert into Aggregation values('" & mt.MeasurementTypeID & "_WEEKRANKBH_" & RankList(Inx) & "','" & PackRel & "','s_" & mt.MeasurementTypeID & "_WEEKRANKBH_" & RankList(Inx) & "','b_" & PackRel & "_WEEK','s_" & mt.MeasurementTypeID & "_WEEKRANKBH_" & RankList(Inx) & "_REAGG','b_" & PackRel & "_WEEK_REAGG'," & "1,1," & "'RANKBH','WEEK');")
                        PrintLine(iFileNum, "insert into Aggregation values('" & mt.MeasurementTypeID & "_MONTHRANKBH_" & RankList(Inx) & "','" & PackRel & "','s_" & mt.MeasurementTypeID & "_MONTHRANKBH_" & RankList(Inx) & "','b_" & PackRel & "_MONTH','s_" & mt.MeasurementTypeID & "_MONTHRANKBH_" & RankList(Inx) & "_REAGG','b_" & PackRel & "_MONTH_REAGG'," & "1,1," & "'RANKBH','MONTH');")
                    End If
                Next Inx
            End If
            If mt.RankTable = True AndAlso mt.ElementBusyHours <> "" Then
                RankList = Split(mt.ElementBusyHours, ",")
                For Inx = 0 To UBound(RankList)
                    If RankList(Inx) <> "" Then
                        PrintLine(iFileNum, "insert into Aggregation values('" & mt.MeasurementTypeID & "_RANKBH_" & RankList(Inx) & "','" & PackRel & "','s_" & mt.MeasurementTypeID & "_RANKBH_" & RankList(Inx) & "','b_" & PackRel & "_DAY','s_" & mt.MeasurementTypeID & "_RANKBH_" & RankList(Inx) & "_REAGG','b_" & PackRel & "_DAY_REAGG'," & "1,2," & "'RANKBH','DAY');")
                        PrintLine(iFileNum, "insert into Aggregation values('" & mt.MeasurementTypeID & "_WEEKRANKBH_" & RankList(Inx) & "','" & PackRel & "','s_" & mt.MeasurementTypeID & "_WEEKRANKBH_" & RankList(Inx) & "','b_" & PackRel & "_WEEK','s_" & mt.MeasurementTypeID & "_WEEKRANKBH_" & RankList(Inx) & "_REAGG','b_" & PackRel & "_WEEK_REAGG'," & "1,2," & "'RANKBH','WEEK');")
                        PrintLine(iFileNum, "insert into Aggregation values('" & mt.MeasurementTypeID & "_MONTHRANKBH_" & RankList(Inx) & "','" & PackRel & "','s_" & mt.MeasurementTypeID & "_MONTHRANKBH_" & RankList(Inx) & "','b_" & PackRel & "_MONTH','s_" & mt.MeasurementTypeID & "_MONTHRANKBH_" & RankList(Inx) & "_REAGG','b_" & PackRel & "_MONTH_REAGG'," & "1,2," & "'RANKBH','MONTH');")
                    End If
                Next Inx
            End If
        Next mt_count

        PrintLine(iFileNum, "")

    End Sub
    Function PrintAggregationRule(ByVal RuleID As Integer, ByVal TargetLevel As String, ByVal SourceLevel As String, ByVal TargetMeas As String, ByVal SourceMeas As String, ByVal AggrType As String, ByVal AggrLevel As String) As String

        Dim InsertColumns As String
        InsertColumns = "insert into AggregationRule (aggregation, versionid, ruleid, target_mtableid, target_type,target_level, target_table, source_mtableid, source_type, source_level, source_table, ruletype, aggregationscope) values ('"

        If TargetLevel = "DAYBH" AndAlso AggrLevel = "WEEK" Then
            PrintAggregationRule = InsertColumns & _
            TargetMeas & "_" & "WEEKBH" & "','" & PackRel & "'," & RuleID & ",'" & _
            PackRel & ":" & TargetMeas + ":" & TargetLevel & "','" & _
            TargetMeas & "','" & TargetLevel & "','" & TargetMeas & "_" & TargetLevel & "','" & _
            PackRel & ":" & SourceMeas + ":" & SourceLevel & "','" & _
            SourceMeas & "','" & SourceLevel & "','" & SourceMeas & "_" & SourceLevel & "'," & _
            "'" & AggrType & "','" & AggrLevel & "');"
        ElseIf TargetLevel = "DAYBH" AndAlso AggrLevel = "MONTH" Then
            PrintAggregationRule = InsertColumns & _
            TargetMeas & "_" & "MONTHBH" & "','" & PackRel & "'," & RuleID & ",'" & _
            PackRel & ":" & TargetMeas + ":" & TargetLevel & "','" & _
            TargetMeas & "','" & TargetLevel & "','" & TargetMeas & "_" & TargetLevel & "','" & _
            PackRel & ":" & SourceMeas + ":" & SourceLevel & "','" & _
            SourceMeas & "','" & SourceLevel & "','" & SourceMeas & "_" & SourceLevel & "'," & _
            "'" & AggrType & "','" & AggrLevel & "');"
        Else
            PrintAggregationRule = InsertColumns & _
            TargetMeas & "_" & TargetLevel & "','" & PackRel & "'," & RuleID & ",'" & _
            PackRel & ":" & TargetMeas + ":" & TargetLevel & "','" & _
            TargetMeas & "','" & TargetLevel & "','" & TargetMeas & "_" & TargetLevel & "','" & _
            PackRel & ":" & SourceMeas + ":" & SourceLevel & "','" & _
            SourceMeas & "','" & SourceLevel & "','" & SourceMeas & "_" & SourceLevel & "'," & _
            "'" & AggrType & "','" & AggrLevel & "');"
        End If

    End Function
    Function PrintRankAggregationRule(ByVal RuleID As Integer, ByVal RankInx As String, ByVal TargetLevel As String, ByVal SourceLevel As String, ByVal TargetMeas As String, ByVal SourceMeas As String, ByVal AggrType As String, ByVal AggrLevel As String) As String

        Dim InsertColumns As String
        InsertColumns = "insert into AggregationRule (aggregation, versionid, ruleid, target_mtableid, target_type,target_level, target_table, source_mtableid, source_type, source_level, source_table, ruletype, aggregationscope,bhtype) values ('"

        If AggrLevel = "WEEK" Then
            PrintRankAggregationRule = InsertColumns & _
            TargetMeas & "_" & AggrLevel & TargetLevel & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
            PackRel & ":" & TargetMeas + ":" & TargetLevel & "','" & _
            TargetMeas & "','" & TargetLevel & "','" & TargetMeas & "_" & TargetLevel & "','" & _
            PackRel & ":" & SourceMeas + ":" & SourceLevel & "','" & _
            SourceMeas & "','" & SourceLevel & "','" & SourceMeas & "_" & SourceLevel & "'," & _
            "'" & AggrType & "','" & AggrLevel & "','" & RankInx & "');"
        ElseIf AggrLevel = "MONTH" Then
            PrintRankAggregationRule = InsertColumns & _
            TargetMeas & "_" & AggrLevel & TargetLevel & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
            PackRel & ":" & TargetMeas + ":" & TargetLevel & "','" & _
            TargetMeas & "','" & TargetLevel & "','" & TargetMeas & "_" & TargetLevel & "','" & _
            PackRel & ":" & SourceMeas + ":" & SourceLevel & "','" & _
            SourceMeas & "','" & SourceLevel & "','" & SourceMeas & "_" & SourceLevel & "'," & _
            "'" & AggrType & "','" & AggrLevel & "','" & RankInx & "');"
        Else
            PrintRankAggregationRule = InsertColumns & _
            TargetMeas & "_" & TargetLevel & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
            PackRel & ":" & TargetMeas + ":" & TargetLevel & "','" & _
            TargetMeas & "','" & TargetLevel & "','" & TargetMeas & "_" & TargetLevel & "','" & _
            PackRel & ":" & SourceMeas + ":" & SourceLevel & "','" & _
            SourceMeas & "','" & SourceLevel & "','" & TargetMeas & "_RANKBH_" & RankInx & "'," & _
            "'" & AggrType & "','" & AggrLevel & "','" & RankInx & "');"
        End If


    End Function
    Function PrintViewAggregationRule(ByVal RuleID As Integer, ByVal RankInx As String, ByVal TargetLevel As String, ByVal SourceLevel As String, ByVal TargetMeas As String, ByVal SourceMeas As String, ByVal AggrType As String, ByVal AggrLevel As String) As String

        Dim InsertColumns As String
        InsertColumns = "insert into AggregationRule (aggregation, versionid, ruleid, target_mtableid, target_type,target_level, target_table, source_mtableid, source_type, source_level, source_table, ruletype, aggregationscope) values ('"

        PrintViewAggregationRule = InsertColumns & _
        TargetMeas & "_" & TargetLevel & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
        PackRel & ":" & TargetMeas + ":" & TargetLevel & "','" & _
        TargetMeas & "','" & TargetLevel & "','" & TargetMeas & "_" & TargetLevel & "','" & _
        PackRel & ":" & SourceMeas + ":" & SourceLevel & "','" & _
        SourceMeas & "','" & SourceLevel & "','" & TargetMeas & "_CALC_" & RankInx & "'," & _
        "'" & AggrType & "','" & AggrLevel & "');"


    End Function
    Sub Metadata_V4Compatibility(ByVal iFileNum As Short)

        PrintLine(iFileNum, "-- V4 Compatibility")
        PrintLine(iFileNum, "insert into storageinfo (STORAGEID,TYPEID,TABLELEVEL,BASETABLENAME, STORAGETIME,PARTITIONSIZE,SEEDTIME,STATUS) select SUBSTRING ( mtableid, LOCATE( mtableid, ':',LOCATE( mtableid, ':')+1)+1 ), typeid, tablelevel, basetablename, 9999, 9999, null, 1 from measurementtable where mtableid like '" & PackRel & ":%' and SUBSTRING ( mtableid, LOCATE( mtableid, ':',LOCATE( mtableid, ':')+1)+1 ) not in (select distinct STORAGEID from StorageInfo);")
        PrintLine(iFileNum, "insert into measurementdata (STORAGEID,DATANAME,COLNUMBER,DATATYPE, DATASIZE,DATASCALE,UNIQUEVALUE,NULLABLE, INDEXES,DESCRIPTION,DATAID,RELEASEID) select SUBSTRING ( mtableid, LOCATE( mtableid, ':',LOCATE( mtableid, ':')+1)+1 ), dataname, colnumber, datatype, datasize, datascale, uniquevalue, nullable, indexes, description, dataid, releaseid from measurementcolumn where mtableid like '" & PackRel & ":%' and dataname not in (select distinct DATANAME from MeasurementData where STORAGEID=SUBSTRING ( measurementcolumn.mtableid, LOCATE( measurementcolumn.mtableid, ':',LOCATE( measurementcolumn.mtableid, ':')+1)+1 ));")
        PrintLine(iFileNum, "insert into referencetype (TYPEID,TYPENAME,DESCRIPTION,STATUS,VERSIONID) select SUBSTRING ( typeid, LOCATE( typeid, ':',LOCATE( typeid, ':')+1)+1 ), typename, description, 0,versionid from referencetable where typeid like '" & PackRel & ":%'and SUBSTRING ( typeid, LOCATE( typeid, ':',LOCATE( typeid, ':')+1)+1 ) not in (select distinct TYPEID from referencetype);")
        PrintLine(iFileNum, "insert into referencedata (TYPEID,DATANAME,COLNUMBER,DATATYPE, DATASIZE,DATASCALE,UNIQUEVALUE,NULLABLE, INDEXES,UNIQUEKEY) select SUBSTRING ( typeid, LOCATE( typeid, ':',LOCATE( typeid, ':')+1)+1 ), dataname, colnumber, datatype, datasize, datascale, uniquevalue, nullable, indexes, uniquekey from referencecolumn where typeid like '" & PackRel & ":%' and dataname not in (select distinct DATANAME from ReferenceData where TYPEID=SUBSTRING ( referencecolumn.typeid, LOCATE( referencecolumn.typeid, ':',LOCATE( referencecolumn.typeid, ':')+1)+1 ));")
        PrintLine(iFileNum, "insert into duplicatemonitoring (STORAGEID,GROUP_FIELD,GROUP_ORDER) select SUBSTRING ( mtableid, LOCATE( mtableid, ':',LOCATE( mtableid, ':')+1)+1 ), dataname, colnumber from measurementcolumn where mtableid like '" & PackRel & ":%' and uniquekey = 1 and dataname not in (select distinct DATANAME from Duplicatemonitoring where STORAGEID=SUBSTRING ( mtableid, LOCATE( mtableid, ':',LOCATE( mtableid, ':')+1)+1 ));")
        PrintLine(iFileNum, "")

    End Sub
    Sub TechPack_ExtraInserts(ByVal iFileNum As Short)

        'PrintLine(iFileNum, "-- Extra inserts")

        On Error Resume Next
        'Wksht = Nothing
        'Wksht = ExcelApp.Workbooks(1).Worksheets("Inserts")
        'If Wksht Is Nothing Then

        'Else
        'Rng = Wksht.Range("A:A")
        'RowCount = getRowCount(Rng, 10, 1)

        'For RowNum = 10 To RowCount
        'If Rng.Cells._Default(RowNum, 1).Value <> "" Then
        'PrintLine(iFileNum, Rng.Cells._Default(RowNum, 1).Value)
        'End If
        'Next RowNum
        'End If

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Inserts$A9:A60000]", tpConn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                PrintLine(iFileNum, dbReader.GetValue(0).ToString())
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

        PrintLine(iFileNum, "")

    End Sub
    Sub TechPack_ExtraMetadataInserts(ByVal iFileNum As Short)

        PrintLine(iFileNum, "-- Extra inserts")

        On Error Resume Next
        'Wksht = Nothing
        'Wksht = ExcelApp.Workbooks(1).Worksheets("Metadata Inserts")
        'If Wksht Is Nothing Then

        'Else
        'Rng = Wksht.Range("A:A")
        'RowCount = getRowCount(Rng, 10, 1)

        'For RowNum = 10 To RowCount
        'If Rng.Cells._Default(RowNum, 1).Value <> "" Then
        'PrintLine(iFileNum, Rng.Cells._Default(RowNum, 1).Value)
        'End If
        'Next RowNum
        'End If

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Metadata Inserts$A9:A60000]", tpConn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                PrintLine(iFileNum, dbReader.GetValue(0).ToString())
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

        PrintLine(iFileNum, "")

    End Sub
    Sub TechPack_BusyHourInserts(ByVal iFileNum As Short)

        Dim BHInserts As String
        Dim comb_BHs As BusyHours
        Dim comb_bh_count As Integer

        PrintLine(iFileNum, "-- Busy Hour Type inserts")

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.RankTable = True Then

                bhs = New BusyHours

                comb_BHs = mt.ObjectBHs
                For comb_bh_count = 1 To comb_BHs.Count
                    bhs.AddItem(comb_BHs.Item(comb_bh_count))
                Next comb_bh_count

                comb_BHs = mt.ElementBHs
                For comb_bh_count = 1 To comb_BHs.Count
                    bhs.AddItem(comb_BHs.Item(comb_bh_count))
                Next comb_bh_count

                For bh_count = 1 To bhs.Count

                    bh = bhs.Item(bh_count)
                    bhobj = bh.BHObject
                    bh_type = bh.BHType

                    BHInserts = "INSERT INTO " & Replace(mt.MeasurementTypeID, "DC_", "DIM_") & "_BHTYPE ( BHTYPE,DESCRIPTION ) values ('"
                    BHInserts &= bh.Name & "','"
                    If bhobj.Type = "element" Then
                        BHInserts &= "Element " & bh_type.Description & "');"
                    End If
                    If bhobj.Type = "object" Then
                        BHInserts &= bhobj.BHObject & " " & bh_type.Description & "');"
                    End If

                    PrintLine(iFileNum, BHInserts)

                Next bh_count

            End If

        Next mt_count

        PrintLine(iFileNum, "")

    End Sub
    Sub TechPack_BusyHourViews(ByVal iFileNum As Short)

        Dim BHViews As String
        Dim rank_mts As MeasurementTypes
        Dim rank_mt As MeasurementTypes.MeasurementType
        Dim rank_mt_count As Integer

        Dim rank_BHs As BusyHours
        Dim rank_BH As BusyHours.BusyHour
        Dim rank_bh_count As Integer
        Dim rank_bh_object As BHObjects.BHObject
        Dim key_values As String
        Dim Found As Boolean

        Dim comb_BHs As BusyHours
        Dim comb_bh_count As Integer

        Dim BHTables() As String
        Dim BHKeyValues() As String
        Dim BHTable As String
        Dim MeasTable As Boolean
        Dim CountTable As Boolean
        Dim Inx As Integer

        rank_mts = mts

        PrintLine(iFileNum, "-- Busy Hour Rank Views")

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.RankTable = True Then

                bhs = New BusyHours

                comb_BHs = mt.ObjectBHs
                For comb_bh_count = 1 To comb_BHs.Count
                    bhs.AddItem(comb_BHs.Item(comb_bh_count))
                Next comb_bh_count

                comb_BHs = mt.ElementBHs
                For comb_bh_count = 1 To comb_BHs.Count
                    bhs.AddItem(comb_BHs.Item(comb_bh_count))
                Next comb_bh_count

                For bh_count = 1 To bhs.Count

                    bh = bhs.Item(bh_count)
                    bhobj = bh.BHObject
                    bh_type = bh.BHType


                    BHViews = "CREATE VIEW " & mt.MeasurementTypeID & "_RANKBH_" & bh.Name & " ( "
                    BHViews &= bhobj.Keys & "DATE_ID,HOUR_ID,BHTYPE,BHVALUE,PERIOD_DURATION )" & ChrW(10)
                    BHViews &= "as SELECT" & ChrW(10)

                    Found = False
                    For rank_mt_count = 1 To rank_mts.Count
                        If Found = False Then
                            rank_mt = rank_mts.Item(rank_mt_count)

                            rank_BHs = rank_mt.RankBHs
                            For rank_bh_count = 1 To rank_BHs.Count
                                rank_BH = rank_BHs.Item(rank_bh_count)

                                If rank_BH.Name = bh.Name Then
                                    rank_bh_object = bh.BHObject
                                    key_values = rank_bh_object.KeyValues
                                    Found = True
                                    Exit For
                                End If
                            Next rank_bh_count
                        Else
                            Exit For
                        End If
                    Next rank_mt_count

                    BHViews &= key_values & ",DATE_ID,HOUR_ID,'" & bh.Name & "'," & ChrW(10)
                    BHViews &= bh_type.BHCriteria & "," & ChrW(10)
                    BHViews &= "sum(PERIOD_DURATION)" & ChrW(10)

                    'määritellään kaikki taulut ja käydään mittaustyypeistä läpi mihin kuuluu raw_act mukaan
                    BHViews &= "FROM "
                    BHTables = Split(bh_type.BHSource, ",")
                    For Inx = 0 To UBound(BHTables)
                        If Inx > 0 Then
                            BHViews &= ","
                        End If

                        MeasTable = False
                        BHTable = BHTables(Inx)

                        CountTable = False


                        For rank_mt_count = 1 To rank_mts.Count
                            rank_mt = rank_mts.Item(rank_mt_count)
                            If rank_mt.MeasurementTypeID = BHTable Then
                                If rank_mt.CountTable = True Then
                                    CountTable = True
                                End If
                                MeasTable = True
                                Exit For
                            End If
                        Next rank_mt_count

                        If MeasTable = True Then
                            If UBound(BHTables) > 0 Then
                                If CountTable = True Then
                                    BHViews &= BHTable & "_COUNT_ACT" & " src" & (Inx + 1).ToString
                                Else
                                    BHViews &= BHTable & "_RAW_ACT" & " src" & (Inx + 1).ToString
                                End If
                            Else
                                If CountTable = True Then
                                    BHViews &= BHTable & "_COUNT_ACT"
                                Else
                                    BHViews &= BHTable & "_RAW_ACT"
                                End If
                            End If
                        Else
                            If UBound(BHTables) > 0 Then
                                BHViews &= BHTable & " src" & (Inx + 1).ToString
                            Else
                                BHViews &= BHTable
                            End If
                        End If
                    Next Inx

                    BHViews &= ChrW(10)
                    If bh_type.BHWhere <> "" Then
                        BHViews &= bh_type.BHWhere & ChrW(10)
                    End If
                    BHViews &= "GROUP BY" & ChrW(10)

                    If UBound(BHTables) > 0 Then

                        BHKeyValues = Split(key_values, ",")

                        For Inx = 0 To UBound(BHKeyValues)
                            If BHKeyValues(Inx) <> "" Then
                                If Inx > 0 Then
                                    BHViews &= ","
                                End If
                                BHViews &= "src1." & BHKeyValues(Inx)
                            End If

                        Next Inx

                        BHViews &= ",DATE_ID,HOUR_ID,'" & bh.Name & "';"
                    Else
                        BHViews &= key_values & ",DATE_ID,HOUR_ID,'" & bh.Name & "';"
                    End If

                    PrintLine(iFileNum, BHViews)
                    PrintLine(iFileNum, "")

                Next bh_count

            End If

        Next mt_count

        PrintLine(iFileNum, "")

    End Sub
    Sub TechPack_TopologyViews(ByVal iFileNum As Short)

        PrintLine(iFileNum, "-- Reference views")

        For rt_count = 1 To rts.Count
            rt = rts.Item(rt_count)
            rt.PrintTopologyView(iFileNum)
        Next rt_count

        PrintLine(iFileNum, "")

    End Sub
    Sub TechPack_ExtraViews(ByVal iFileNum As Short)


        PrintLine(iFileNum, "-- Extra views")

        On Error Resume Next
        'Wksht = Nothing
        'Wksht = ExcelApp.Workbooks(1).Worksheets("Views")
        'If Wksht Is Nothing Then

        'Else
        'Rng = Wksht.Range("A:B")
        'RowCount = getRowCount(Rng, 10, 1)

        'For RowNum = 10 To RowCount
        'If Rng.Cells._Default(RowNum, 1).Value <> "" Then
        'PrintLine(iFileNum, Rng.Cells._Default(RowNum, 2).Value)
        'End If
        'Next RowNum
        'End If

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Views$A9:B60000]", tpConn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                PrintLine(iFileNum, dbReader.GetValue(1).ToString())
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

        PrintLine(iFileNum, "")

    End Sub
    Sub TechPack_ExtraProcedures(ByVal iFileNum As Short)

        PrintLine(iFileNum, "-- Extra procedures")

        On Error Resume Next
        'Wksht = Nothing
        'Wksht = ExcelApp.Workbooks(1).Worksheets("Procedures")
        'If Wksht Is Nothing Then

        'Else
        'Rng = Wksht.Range("A:B")
        'RowCount = getRowCount(Rng, 10, 1)

        'For RowNum = 10 To RowCount
        'If Rng.Cells._Default(RowNum, 1).Value <> "" Then
        'PrintLine(iFileNum, Rng.Cells._Default(RowNum, 2).Value)
        'End If
        'Next RowNum
        'End If

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Procedures$A9:B60000]", tpConn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                PrintLine(iFileNum, dbReader.GetValue(1).ToString())
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()

        PrintLine(iFileNum, "")

    End Sub
    Private Function Initialize_Classes() As Integer

        Try

            'Wksht2 = ExcelApp.Workbooks(1).Worksheets("Counters")
            'Rng2 = Wksht2.Range("A:A")
            'RowCount2 = getRowCount(Rng2, 10, 1)
            'RowCount2 = 10

            'da = New System.Data.OleDb.OleDbDataAdapter("Select * From [Counters$A9:A60000]", tpConn)
            'ds = New System.Data.DataSet
            'da.Fill(ds)
            'For Each dr In ds.Tables(0).Rows 'Show results in output window
            'If dr.IsNull(0) Then
            'Exit For
            'Else
            'RowCount2 += 1
            'End If
            'Next
            '" & RowCount2 + 10 & "
            all_cnts = New Counters
            all_cnts.getCounters(tpConn, dbCommand, dbReader)
            'all_cnts = New Counters
            'da = New System.Data.OleDb.OleDbDataAdapter("Select * From [Counters$A9:M60000]", tpConn)
            'ds = New System.Data.DataSet

            'dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Counters$A9:M60000]", tpConn)
            'dbReader = dbCommand.ExecuteReader()
            'While (dbReader.Read())
            'If dbReader.GetValue(0).ToString() = "" Then
            'Exit While
            'Else
            'cnt = New Counters.Counter
            'cnt.MeasurementTypeID = dbReader.GetValue(0).ToString()
            'cnt.UnivObject = dbReader.GetValue(7).ToString()
            'cnt.CounterName = dbReader.GetValue(1).ToString()
            'cnt.Description = dbReader.GetValue(2).ToString()
            'cnt.Source = dbReader.GetValue(3).ToString()
            'Call getDatatype(dbReader.GetValue(4).ToString())
            'cnt.Datatype = Datatype
            'cnt.Datasize = Datasize
            'cnt.Datascale = Datascale
            'If cnt.Datatype = "NOT FOUND" OrElse cnt.Datasize = "Err" Then
            'Throw New Exception("Data type in counter " & cnt.CounterName & " in measurement " & cnt.MeasurementTypeID & " is not defined correctly. Please check documentation for further details.")
            'End If
            'cnt.TimeAggr = dbReader.GetValue(5).ToString()
            'cnt.GroupAggr = dbReader.GetValue(6).ToString()
            'cnt.CountAggr = dbReader.GetValue(11).ToString()
            'cnt.UnivClass = dbReader.GetValue(10).ToString()
            'cnt.Release = dbReader.GetValue(9).ToString()
            'cnt.SpecialIndex = dbReader.GetValue(12).ToString()
            'all_cnts.AddItem(cnt)
            'End If
            'End While
            'dbReader.Close()
            'dbCommand.Dispose()
            'da.Fill(ds)
            'For Each dr In ds.Tables(0).Rows 'Show results in output window
            'If dr.IsNull(0) Then
            'Exit For
            'Else
            'cnt = New Counters.Counter
            'cnt.MeasurementTypeID = IIf(dr.IsNull(0), "", dr(0).ToString())
            'cnt.UnivObject = IIf(dr.IsNull(7), "", dr(7).ToString())
            'cnt.CounterName = IIf(dr.IsNull(1), "", dr(1).ToString())
            'cnt.Description = IIf(dr.IsNull(2), "", dr(2).ToString())
            'cnt.Source = IIf(dr.IsNull(3), "", dr(3).ToString())
            'Call getDatatype(IIf(dr.IsNull(4), "", dr(4).ToString()))
            'cnt.Datatype = Datatype
            'cnt.Datasize = Datasize
            'cnt.Datascale = Datascale
            'If cnt.Datatype = "NOT FOUND" OrElse cnt.Datasize = "Err" Then
            'Throw New Exception("Data type in counter " & cnt.CounterName & " in measurement " & cnt.MeasurementTypeID & " is not defined correctly. Please check documentation for further details.")
            'End If
            'cnt.TimeAggr = IIf(dr.IsNull(5), "", dr(5).ToString())
            'cnt.GroupAggr = IIf(dr.IsNull(6), "", dr(6).ToString())
            'cnt.CountAggr = IIf(dr.IsNull(11), "", dr(11).ToString())
            'cnt.UnivClass = IIf(dr.IsNull(10), "", dr(10).ToString())
            'cnt.Release = IIf(dr.IsNull(9), "", dr(9).ToString())
            'cnt.SpecialIndex = IIf(dr.IsNull(12), "", dr(12).ToString())
            'all_cnts.AddItem(cnt)
            'End If
            'Next


            'Wksht2 = ExcelApp.Workbooks(1).Worksheets("Counters")
            'Rng2 = Wksht2.Range("A:M")
            'RowCount2 = getRowCount(Rng2, 10, 1)

            '60 % of run time
            'all_cnts = New Counters
            'For RowNum2 = 10 To Rng2.Count
            'If Rng2.Cells(RowNum2, 1).Value <> "" Then
            'cnt = New Counters.Counter
            'cnt.MeasurementTypeID = Rng2.Cells(RowNum2, 1).Value
            'cnt.UnivObject = Rng2.Cells(RowNum2, 8).Value
            'cnt.CounterName = Rng2.Cells(RowNum2, 2).Value
            'cnt.Description = Rng2.Cells(RowNum2, 3).Value
            'cnt.Source = Rng2.Cells(RowNum2, 4).Value

            'Call getDatatype(Rng2.Cells(RowNum2, 5).Value)
            'cnt.Datatype = Datatype
            'cnt.Datasize = Datasize
            'cnt.Datascale = Datascale

            'If cnt.Datatype = "NOT FOUND" OrElse cnt.Datasize = "Err" Then
            'Throw New Exception("Row: " & RowNum2 & ". Data type in counter " & cnt.CounterName & " in measurement " & cnt.MeasurementTypeID & " is not defined correctly. Please check documentation for further details.")
            'End If

            'cnt.TimeAggr = Rng2.Cells(RowNum2, 6).Value
            'cnt.GroupAggr = Rng2.Cells(RowNum2, 7).Value
            'cnt.CountAggr = Rng2.Cells(RowNum2, 12).Value
            'cnt.UnivClass = Rng2.Cells(RowNum2, 11).Value
            'cnt.Release = Rng2.Cells(RowNum2, 10).Value
            'cnt.SpecialIndex = Rng2.Cells(RowNum2, 13).Value
            'all_cnts.AddItem(cnt)
            'Else
            'Exit For
            'End If
            'Next RowNum2
            ' to here

            all_cnt_keys = New CounterKeys
            all_cnt_keys.getCounterKeys(tpConn, dbCommand, dbReader)
            'dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Keys$A11:O60000]", tpConn)
            'dbReader = dbCommand.ExecuteReader()
            'While (dbReader.Read())
            'If dbReader.GetValue(0).ToString() = "" Then
            'Exit While
            'Else
            'cnt_key = New CounterKeys.CounterKey
            'cnt_key.MeasurementTypeID = dbReader.GetValue(0).ToString()
            'cnt_key.UnivObject = dbReader.GetValue(11).ToString()
            'cnt_key.CounterKeyName = dbReader.GetValue(1).ToString()
            'cnt_key.Description = dbReader.GetValue(2).ToString()
            'cnt_key.Source = dbReader.GetValue(4).ToString()
            'Call getDatatype(dbReader.GetValue(5).ToString())
            'cnt_key.Datatype = Datatype
            'cnt_key.Datasize = Datasize
            'cnt_key.Datascale = Datascale
            'If cnt_key.Datatype = "NOT FOUND" OrElse cnt_key.Datasize = "Err" Then
            'Throw New Exception("Data type in key " & cnt_key.CounterKeyName & " in measurement " & cnt_key.MeasurementTypeID & " is not defined correctly. Please check documentation for further details.")
            'End If
            'cnt_key.MaxAmount = dbReader.GetValue(9).ToString()
            'cnt_key.IQIndex = dbReader.GetValue(10).ToString()
            'cnt_key.DupConstraint = dbReader.GetValue(6).ToString()
            'cnt_key.Element = dbReader.GetValue(13).ToString()
            'cnt_key.Release = dbReader.GetValue(14).ToString()
            'If cnt_key.DupConstraint = 1 Then
            'cnt_key.Nullable = 0
            'Else
            'cnt_key.Nullable = 1
            'End If
            'all_cnt_keys.AddItem(cnt_key)
            'End If
            'End While
            'dbReader.Close()
            'dbCommand.Dispose()

            'Wksht2 = ExcelApp.Workbooks(1).Worksheets("Keys")
            'Rng2 = Wksht2.Range("A:O")
            'RowCount2 = getRowCount(Rng2, 12, 1)
            'For RowNum2 = 12 To RowCount2
            'If Rng2.Cells(RowNum2, 1).Value <> "" Then
            'cnt_key = New CounterKeys.CounterKey
            'cnt_key.MeasurementTypeID = Rng2.Cells(RowNum2, 1).Value
            'cnt_key.UnivObject = Rng2.Cells(RowNum2, 12).Value
            'cnt_key.CounterKeyName = Rng2.Cells(RowNum2, 2).Value
            'cnt_key.Description = Rng2.Cells(RowNum2, 3).Value
            'cnt_key.Source = Rng2.Cells(RowNum2, 5).Value

            'Call getDatatype(Rng2.Cells(RowNum2, 6).Value)
            'cnt_key.Datatype = Datatype
            'cnt_key.Datasize = Datasize
            'cnt_key.Datascale = Datascale

            'If cnt_key.Datatype = "NOT FOUND" OrElse cnt_key.Datasize = "Err" Then
            'Throw New Exception("Row: " & RowNum2 & ". Data type in key " & cnt_key.CounterKeyName & " in measurement " & cnt_key.MeasurementTypeID & " is not defined correctly. Please check documentation for further details.")
            'End If

            'cnt_key.MaxAmount = Rng2.Cells(RowNum2, 10).Text
            'cnt_key.IQIndex = Rng2.Cells(RowNum2, 11).Value
            'cnt_key.DupConstraint = Rng2.Cells(RowNum2, 7).Value
            'cnt_key.Element = Rng2.Cells(RowNum2, 14).Value
            'cnt_key.Release = Rng2.Cells(RowNum2, 15).Value

            'If cnt_key.DupConstraint = 1 Then
            'cnt_key.Nullable = 0
            'Else
            '    cnt_key.Nullable = 1
            'End If
            '
            'all_cnt_keys.AddItem(cnt_key)
            'Else
            'Exit For
            'End If
            'Next RowNum2

            pub_keys = New PublicKeys
            dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Public Keys$A9:K60000]", baseConn)
            dbReader = dbCommand.ExecuteReader()
            While (dbReader.Read())
                If dbReader.GetValue(0).ToString() = "" Then
                    Exit While
                Else
                    pub_key = New PublicKeys.PublicKey
                    pub_key.KeyType = dbReader.GetValue(0).ToString()
                    pub_key.IQIndex = dbReader.GetValue(7).ToString()
                    pub_key.PublicKeyName = dbReader.GetValue(1).ToString()
                    pub_key.Description = dbReader.GetValue(2).ToString()
                    pub_key.Source = dbReader.GetValue(4).ToString()
                    Call getDatatype(dbReader.GetValue(5).ToString())
                    pub_key.Datatype = Datatype
                    pub_key.Datasize = Datasize
                    pub_key.Datascale = Datascale
                    If pub_key.Datatype = "NOT FOUND" OrElse pub_key.Datasize = "Err" Then
                        Throw New Exception("Data type in key " & pub_key.PublicKeyName & " in public keys " & pub_key.KeyType & " is not defined correctly. Please check documentation for further details.")
                    End If
                    pub_key.MaxAmount = dbReader.GetValue(6).ToString()
                    pub_key.Nullable = dbReader.GetValue(8).ToString()
                    pub_key.DupConstraint = dbReader.GetValue(9).ToString()
                    pub_keys.AddItem(pub_key)
                End If
            End While
            dbReader.Close()
            dbCommand.Dispose()

            'Wksht2 = ExcelApp.Workbooks(2).Worksheets("Public Keys")
            'Rng2 = Wksht2.Range("A:J")
            'RowCount2 = getRowCount(Rng2, 10, 1)
            'For RowNum2 = 10 To RowCount2
            'If Rng2.Cells._Default(RowNum2, 1).Value <> "" Then

            'pub_key = New PublicKeys.PublicKey
            'pub_key.KeyType = Rng2.Cells._Default(RowNum2, 1).Value
            'pub_key.IQIndex = Rng2.Cells._Default(RowNum2, 8).Value
            'pub_key.PublicKeyName = Rng2.Cells._Default(RowNum2, 2).Value
            'pub_key.Description = Rng2.Cells._Default(RowNum2, 3).Value
            'pub_key.Source = Rng2.Cells._Default(RowNum2, 5).Value

            'Call getDatatype(Rng2.Cells._Default(RowNum2, 6).Value)
            'pub_key.Datatype = Datatype
            'pub_key.Datasize = Datasize
            'pub_key.Datascale = Datascale

            'If pub_key.Datatype = "NOT FOUND" OrElse pub_key.Datasize = "Err" Then
            'Throw New Exception("Row: " & RowNum2 & ". Data type in key " & pub_key.PublicKeyName & " in public keys " & pub_key.KeyType & " is not defined correctly. Please check documentation for further details.")
            'End If

            'pub_key.MaxAmount = Rng2.Cells._Default(RowNum2, 7).Text
            'pub_key.Nullable = Rng2.Cells._Default(RowNum2, 9).Value
            'pub_key.DupConstraint = Rng2.Cells._Default(RowNum2, 10).Value

            'pub_keys.AddItem(pub_key)
            'End If
            'Next RowNum2

            'Wksht2 = ExcelApp.Workbooks(1).Worksheets("BusyHours")
            'Rng2 = Wksht2.Range("A:F")
            'RowCount2 = getRowCount(Rng2, 9, 1)

            Dim ObjectRow As Boolean

            all_bh_types = New BHTypes
            all_bhobjs = New BHObjects
            ObjectRow = False
            dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [BusyHours$A8:F60000]", tpConn)
            dbReader = dbCommand.ExecuteReader()
            While (dbReader.Read())
                If dbReader.GetValue(0).ToString() = "" Then
                    Exit While
                Else
                    If ObjectRow = True Then
                        bhobj = New BHObjects.BHObject
                        bhobj.BHObject = dbReader.GetValue(0).ToString()
                        bhobj.Keys = dbReader.GetValue(1).ToString()
                        bhobj.KeyValues = dbReader.GetValue(2).ToString()
                        bhobj.Type = dbReader.GetValue(3).ToString()
                        all_bhobjs.AddItem(bhobj)
                    End If
                    If LCase(dbReader.GetValue(0).ToString()) = "busy hour object" Then
                        ObjectRow = True
                    End If
                    If ObjectRow = False Then
                        bh_type = New BHTypes.BHType
                        bh_type.BHType = dbReader.GetValue(0).ToString()
                        bh_type.Description = dbReader.GetValue(1).ToString()
                        bh_type.BHSource = dbReader.GetValue(2).ToString()
                        bh_type.BHCriteria = dbReader.GetValue(3).ToString()
                        bh_type.BHWhere = dbReader.GetValue(4).ToString()
                        bh_type.BHObjects = dbReader.GetValue(5).ToString()
                        all_bh_types.AddItem(bh_type)
                    End If
                End If
            End While
            dbReader.Close()
            dbCommand.Dispose()



            'For RowNum2 = 9 To RowCount2
            'If Rng2.Cells._Default(RowNum2, 1).Value <> "" Then
            ' If ObjectRow = True Then
            'bhobj = New BHObjects.BHObject
            'bhobj.BHObject = dbReader.GetValue(0).ToString()
            'bhobj.Keys = Rng2.Cells._Default(RowNum2, 2).Value
            'bhobj.KeyValues = Rng2.Cells._Default(RowNum2, 3).Value
            'bhobj.Type = Rng2.Cells._Default(RowNum2, 4).Value
            'all_bhobjs.AddItem(bhobj)
            'End If
            'If LCase(Rng2.Cells._Default(RowNum2, 1).Value = "busy hour object" Then
            'ObjectRow = True
            'End If
            'If ObjectRow = False Then
            'bh_type = New BHTypes.BHType
            'bh_type.BHType = Rng2.Cells._Default(RowNum2, 1).Value
            'bh_type.Description = Rng2.Cells._Default(RowNum2, 2).Value
            'bh_type.BHSource = Rng2.Cells._Default(RowNum2, 3).Value
            'bh_type.BHCriteria = Rng2.Cells._Default(RowNum2, 4).Value
            'bh_type.BHWhere = Rng2.Cells._Default(RowNum2, 5).Value
            'bh_type.BHObjects = Rng2.Cells._Default(RowNum2, 6).Value
            'all_bh_types.AddItem(bh_type)
            'End If
            'End If
            'Next RowNum2

            'Wksht = ExcelApp.Workbooks(1).Worksheets("MeasurementTypes")
            'Rng = Wksht.Range("A:Q")
            'RowCount = getRowCount(Rng, 9, 1)

            all_mts = New MeasurementTypes

            dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [MeasurementTypes$A8:R60000]", tpConn)
            dbReader = dbCommand.ExecuteReader()
            While (dbReader.Read())
                If dbReader.GetValue(0).ToString() = "" Then
                    Exit While
                Else
                    mt = New MeasurementTypes.MeasurementType
                    mt.MeasurementTypeID = dbReader.GetValue(0).ToString()
                    mt.VendorMeasurement = dbReader.GetValue(3).ToString()
                    mt.Description = dbReader.GetValue(2).ToString()
                    mt.DayAggregation = dbReader.GetValue(8).ToString()
                    mt.ObjectBusyHours = dbReader.GetValue(9).ToString()
                    mt.ElementBusyHours = dbReader.GetValue(10).ToString()
                    mt.RankTable = dbReader.GetValue(12).ToString()
                    mt.CountTable = dbReader.GetValue(16).ToString()
                    mt.PlainTable = dbReader.GetValue(17).ToString()
                    mt.Release = dbReader.GetValue(15).ToString()
                    mt.RelatedMeasurements = dbReader.GetValue(14).ToString()
                    mt.RankTypes = dbReader.GetValue(13).ToString()
                    mt.MeasurementTypeClass = dbReader.GetValue(5).ToString()
                    mt.MeasurementTypeClassDescription = dbReader.GetValue(6).ToString()
                    mt.Vendor = dbReader.GetValue(4).ToString()

                    all_mts.AddItem(mt)
                End If
            End While
            dbReader.Close()
            dbCommand.Dispose()

            'For RowNum = 9 To RowCount

            'If Rng.Cells._Default(RowNum, 1).Value <> "" Then
            'mt = New MeasurementTypes.MeasurementType
            'mt.MeasurementTypeID = Rng.Cells._Default(RowNum, 1).Value
            'mt.VendorMeasurement = Rng.Cells._Default(RowNum, 4).Value
            'mt.Description = Rng.Cells._Default(RowNum, 3).Value
            'mt.DayAggregation = Rng.Cells._Default(RowNum, 9).Value
            'mt.ObjectBusyHours = Rng.Cells._Default(RowNum, 10).Value
            'mt.ElementBusyHours = Rng.Cells._Default(RowNum, 11).Value
            'mt.RankTable = Rng.Cells._Default(RowNum, 13).Value
            'mt.CountTable = Rng.Cells._Default(RowNum, 17).Value
            'mt.PlainTable = Rng.Cells._Default(RowNum, 18).Value
            'mt.Release = Rng.Cells._Default(RowNum, 16).Value
            'mt.RelatedMeasurements = Rng.Cells._Default(RowNum, 15).Value
            'mt.RankTypes = Rng.Cells._Default(RowNum, 14).Value
            'mt.MeasurementTypeClass = Rng.Cells._Default(RowNum, 6).Value
            'mt.MeasurementTypeClassDescription = Rng.Cells._Default(RowNum, 7).Value
            'mt.Vendor = Rng.Cells._Default(RowNum, 5).Value

            'all_mts.AddItem(mt)

            'End If
            'Next RowNum
            Dim PrevMeas As String
            PrevMeas = ""

            mts = New MeasurementTypes

            dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [MeasurementTypes$A8:R60000]", tpConn)
            dbReader = dbCommand.ExecuteReader()
            While (dbReader.Read())
                If dbReader.GetValue(0).ToString() = "" Then
                    Exit While
                Else
                    'For RowNum = 9 To RowCount
                    'If Rng.Cells._Default(RowNum, 1).Value <> Rng.Cells._Default(RowNum - 1, 1).Value AndAlso Rng.Cells._Default(RowNum, 1).Value <> "" Then
                    If dbReader.GetValue(0).ToString() <> PrevMeas Then

                        mt = New MeasurementTypes.MeasurementType

                        Dim KeyCounts As ReleaseCounts = New ReleaseCounts
                        Dim count As Integer
                        Dim RelFound As Boolean
                        Dim KeyCount As ReleaseCounts.ReleaseCount

                        PrevMeas = dbReader.GetValue(0).ToString()
                        mt.MeasurementTypeID = dbReader.GetValue(0).ToString()
                        mt.VendorMeasurement = dbReader.GetValue(3).ToString()
                        mt.Description = dbReader.GetValue(2).ToString()
                        mt.DayAggregation = dbReader.GetValue(8).ToString()
                        mt.ObjectBusyHours = dbReader.GetValue(9).ToString()
                        mt.ElementBusyHours = dbReader.GetValue(10).ToString()
                        mt.RankTable = dbReader.GetValue(12).ToString()
                        mt.CountTable = dbReader.GetValue(16).ToString()
                        mt.PlainTable = dbReader.GetValue(17).ToString()
                        mt.Release = dbReader.GetValue(15).ToString()
                        mt.RelatedMeasurements = dbReader.GetValue(14).ToString()
                        mt.RankTypes = dbReader.GetValue(13).ToString()
                        mt.MeasurementTypeClass = dbReader.GetValue(5).ToString()
                        mt.MeasurementTypeClassDescription = dbReader.GetValue(6).ToString()
                        mt.Vendor = dbReader.GetValue(4).ToString()

                        'mt.MeasurementTypeID = Rng.Cells._Default(RowNum, 1).Value
                        'mt.VendorMeasurement = Rng.Cells._Default(RowNum, 2).Value
                        'mt.Description = Rng.Cells._Default(RowNum, 3).Value
                        'mt.DayAggregation = Rng.Cells._Default(RowNum, 9).Value
                        'mt.ObjectBusyHours = Rng.Cells._Default(RowNum, 10).Value
                        'mt.ElementBusyHours = Rng.Cells._Default(RowNum, 11).Value
                        'mt.RankTable = Rng.Cells._Default(RowNum, 13).Value
                        'mt.CountTable = Rng.Cells._Default(RowNum, 17).Value
                        'mt.PlainTable = Rng.Cells._Default(RowNum, 18).Value
                        'mt.Release = Rng.Cells._Default(RowNum, 16).Value
                        'mt.RelatedMeasurements = Rng.Cells._Default(RowNum, 15).Value
                        'mt.RankTypes = Rng.Cells._Default(RowNum, 14).Value
                        'mt.MeasurementTypeClass = Rng.Cells._Default(RowNum, 6).Value
                        'mt.MeasurementTypeClassDescription = Rng.Cells._Default(RowNum, 7).Value
                        'mt.Vendor = Rng.Cells._Default(RowNum, 5).Value

                        cnts = New Counters
                        For cnt_count = 1 To all_cnts.Count
                            If all_cnts.Item(cnt_count).MeasurementTypeID = mt.MeasurementTypeID Then

                                RelFound = False
                                For count = 1 To KeyCounts.Count
                                    KeyCount = KeyCounts.Item(count)
                                    If KeyCount.Release = all_cnts.Item(cnt_count).Release Then
                                        RelFound = True
                                        Exit For
                                    End If
                                Next count
                                If RelFound = False Then
                                    KeyCount = New ReleaseCounts.ReleaseCount
                                    KeyCount.SetSeed(all_cnts.Item(cnt_count).Release, 100, "")
                                    KeyCounts.AddItem(KeyCount)
                                End If

                                all_cnts.Item(cnt_count).ColNumber = KeyCount.ColCount
                                'Index
                                If all_cnts.Item(cnt_count).Datasize > 255 Then
                                    all_cnts.Item(cnt_count).IndexValue = "-"
                                Else
                                    If all_cnts.Item(cnt_count).SpecialIndex <> "" Then
                                        all_cnts.Item(cnt_count).IndexValue = "HNG," & all_cnts.Item(cnt_count).SpecialIndex
                                    Else
                                        all_cnts.Item(cnt_count).IndexValue = "HNG"
                                    End If
                                End If

                                cnts.AddItem(all_cnts.Item(cnt_count))

                            End If
                        Next cnt_count
                        mt.Counters = cnts

                        KeyCounts = New ReleaseCounts

                        cnt_keys = New CounterKeys
                        For cnt_key_count = 1 To all_cnt_keys.Count
                            If all_cnt_keys.Item(cnt_key_count).MeasurementTypeID = mt.MeasurementTypeID Then

                                'Update Colnumber first
                                RelFound = False
                                For count = 1 To KeyCounts.Count
                                    If KeyCounts.Item(count).Release = all_cnt_keys.Item(cnt_key_count).Release Then
                                        RelFound = True
                                        Exit For
                                    End If
                                Next count

                                If RelFound = False Then
                                    KeyCount = New ReleaseCounts.ReleaseCount
                                    KeyCount.SetSeed(all_cnt_keys.Item(cnt_key_count).Release, 1, "00")
                                    KeyCounts.AddItem(KeyCount)
                                End If

                                all_cnt_keys.Item(cnt_key_count).ColNumber = KeyCount.ColCount

                                cnt_keys.AddItem(all_cnt_keys.Item(cnt_key_count))
                            End If
                        Next cnt_key_count


                        mt.CounterKeys = cnt_keys


                        Dim mt_pub_keys As PublicKeys = New PublicKeys

                        Dim PublicCount As ReleaseCounts.ReleaseCount = New ReleaseCounts.ReleaseCount
                        Dim PrevType As String
                        PrevType = ""

                        For pub_key_count = 1 To pub_keys.Count
                            pub_key = pub_keys.Item(pub_key_count)

                            If pub_key.KeyType <> PrevType Then
                                PublicCount.SetSeed(mt.Release, 50, "0")
                            End If

                            pub_key.ColNumber = PublicCount.ColCount
                            mt_pub_keys.AddItem(pub_key)
                            PrevType = pub_key.KeyType

                        Next pub_key_count

                        mt.PublicKeys = mt_pub_keys

                        Dim BusyHours() As String
                        Dim Inx As Integer
                        Dim BHInfo() As String
                        Dim DefKeys As String
                        Dim TempBHObject As BHObjects.BHObject

                        DefKeys = ""
                        cnt_keys = mt.CounterKeys
                        For cnt_key_count = 1 To cnt_keys.Count
                            cnt_key = cnt_keys.Item(cnt_key_count)
                            DefKeys &= cnt_key.CounterKeyName() & ","
                        Next cnt_key_count

                        bhs = New BusyHours
                        BusyHours = Split(mt.ObjectBusyHours, ",")
                        For Inx = 0 To UBound(BusyHours)
                            If BusyHours(Inx) <> "" Then
                                bh = New BusyHours.BusyHour
                                bh.Name = BusyHours(Inx)

                                BHInfo = Split(BusyHours(Inx), "_", 2)

                                For bht_count = 1 To all_bh_types.Count
                                    bh_type = all_bh_types.Item(bht_count)
                                    If bh_type.BHType = BHInfo(1) Then
                                        bh.BHType = bh_type
                                        Exit For
                                    End If
                                Next bht_count

                                For bho_count = 1 To all_bhobjs.Count
                                    bhobj = all_bhobjs.Item(bho_count)
                                    If bhobj.BHObject = BHInfo(0) AndAlso bhobj.Type = "object" Then
                                        TempBHObject = New BHObjects.BHObject
                                        TempBHObject.copy(bhobj)
                                        bh.BHObject = TempBHObject
                                        Exit For
                                    End If
                                Next bho_count
                                bhobj = bh.BHObject
                                If bhobj Is Nothing Then
                                    Throw New Exception("BH object: " & bh.Name & " is not defined correctly. Please check documentation for further details.")
                                End If
                                If bhobj.Keys <> "" Then
                                    bhobj.Keys &= ","
                                Else
                                    bhobj.Keys = DefKeys
                                End If
                                bhs.AddItem(bh)
                            End If
                        Next Inx
                        mt.ObjectBHs = bhs

                        bhs = New BusyHours
                        BusyHours = Split(mt.ElementBusyHours, ",")
                        For Inx = 0 To UBound(BusyHours)
                            If BusyHours(Inx) <> "" Then
                                bh = New BusyHours.BusyHour
                                bh.Name = BusyHours(Inx)
                                BHInfo = Split(BusyHours(Inx), "_", 2)

                                For bht_count = 1 To all_bh_types.Count
                                    bh_type = all_bh_types.Item(bht_count)
                                    If bh_type.BHType = BHInfo(1) Then
                                        bh.BHType = bh_type
                                        Exit For
                                    End If
                                Next bht_count

                                For bho_count = 1 To all_bhobjs.Count
                                    bhobj = all_bhobjs.Item(bho_count)
                                    If bhobj.BHObject = BHInfo(0) AndAlso bhobj.Type = "element" Then
                                        TempBHObject = New BHObjects.BHObject
                                        TempBHObject.copy(bhobj)
                                        bh.BHObject = TempBHObject
                                        Exit For
                                    End If
                                Next bho_count
                                bhobj = bh.BHObject
                                If bhobj Is Nothing Then
                                    Throw New Exception("BH object: " & bh.Name & " is not defined correctly. Please check documentation for further details.")
                                End If
                                If bhobj.Keys <> "" Then
                                    bhobj.Keys &= ","
                                Else
                                    bhobj.Keys = DefKeys
                                End If
                                bhs.AddItem(bh)
                            End If
                        Next Inx
                        mt.ElementBHs = bhs

                        bhs = New BusyHours
                        BusyHours = Split(mt.RankTypes, ",")
                        For Inx = 0 To UBound(BusyHours)
                            If BusyHours(Inx) <> "" Then
                                bh = New BusyHours.BusyHour
                                bh.Name = BusyHours(Inx)
                                BHInfo = Split(BusyHours(Inx), "_", 2)

                                For bht_count = 1 To all_bh_types.Count
                                    bh_type = all_bh_types.Item(bht_count)
                                    If bh_type.BHType = BHInfo(1) Then
                                        bh.BHType = bh_type
                                        Exit For
                                    End If
                                Next bht_count

                                For bho_count = 1 To all_bhobjs.Count
                                    bhobj = all_bhobjs.Item(bho_count)
                                    If bhobj.BHObject = BHInfo(0) Then
                                        TempBHObject = New BHObjects.BHObject
                                        TempBHObject.copy(bhobj)
                                        bh.BHObject = TempBHObject
                                        Exit For
                                    End If
                                Next bho_count
                                bhobj = bh.BHObject
                                If bhobj.KeyValues <> "" Then
                                    bhobj.KeyValues &= ","
                                Else
                                    bhobj.KeyValues = DefKeys
                                End If
                                bhs.AddItem(bh)
                            End If
                        Next Inx
                        mt.RankBHs = bhs

                        mts.AddItem(mt)

                    End If
                End If
            End While
            'Next RowNum
            dbReader.Close()
            dbCommand.Dispose()

            Dim Description As String
            dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Coversheet$C3:C4]", tpConn)
            dbReader = dbCommand.ExecuteReader()
            While (dbReader.Read())
                SourcePack = dbReader.GetValue(0).ToString()
            End While
            dbReader.Close()
            dbCommand.Dispose()

            dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Coversheet$C6:C7]", tpConn)
            dbReader = dbCommand.ExecuteReader()
            While (dbReader.Read())
                Description = dbReader.GetValue(0).ToString()
            End While
            dbReader.Close()
            dbCommand.Dispose()

            dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Coversheet$C8:C9]", tpConn)
            dbReader = dbCommand.ExecuteReader()
            While (dbReader.Read())
                TPVersion = dbReader.GetValue(0).ToString()
            End While
            dbReader.Close()
            dbCommand.Dispose()

            PackRel = SourcePack & ":" & TPVersion
            Version_Information = "-- Versioning" & ChrW(10)
            Version_Information &= "insert into versioning (versionid, description, status, techpack_name, techpack_version) values ('" & _
            PackRel & "','" & Description & "',1,'" & SourcePack & "','" & TPVersion & "');"
            Version_Information = ChrW(10) & Version_Information & ChrW(10)


            'Wksht = ExcelApp.Workbooks(1).Worksheets("Coversheet")
            'Rng = Wksht.Range("A:B")

            'If Rng.Cells._Default(9, 3).Value = "" OrElse Rng.Cells._Default(7, 3).Value = "" Then
            'MsgBox("Please fill following information in Coversheet. " & ChrW(10) & "Package Release (C9)" & ChrW(10) & "Description (C7)")
            'Application.Exit()
            'Else
            'SourcePack = Rng.Cells._Default(4, 3).Value
            'TPVersion = Rng.Cells._Default(9, 3).Value
            'PackRel = Rng.Cells._Default(4, 3).Value & ":" & Rng.Cells._Default(9, 3).Value
            'Version_Information = "-- Versioning" & ChrW(10)
            'Version_Information &= "insert into versioning (versionid, description, status, techpack_name, techpack_version) values ('" & PackRel & "','" & Rng.Cells._Default(7, 3).Value & "',1,'" & Rng.Cells._Default(4, 3).Value & "','" & Rng.Cells._Default(9, 3).Value & "');"
            'Version_Information = ChrW(10) & Version_Information & ChrW(10)
            'End If

            Dim TableRow As Boolean
            Dim ColRow As Boolean
            Dim First As Boolean

            rts = New ReferenceTypes
            TableRow = False
            First = True

            dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [TopologyData$A11:G60000]", tpConn)
            dbReader = dbCommand.ExecuteReader()
            While (dbReader.Read())
                If dbReader.GetValue(0).ToString() = "" Then
                    rt.ReferenceDatas = rds
                    rts.AddItem(rt)
                    Exit While
                Else
                    If TableRow = True Then
                        rt = New ReferenceTypes.ReferenceType
                        rt.ReferenceTypeID = dbReader.GetValue(0).ToString()
                        rt.Description = dbReader.GetValue(1).ToString()
                        rt.Type = dbReader.GetValue(3).ToString()
                        rt.Update = dbReader.GetValue(2).ToString()
                        rt.CurrentRequired = dbReader.GetValue(4).ToString()
                        TableRow = False
                    ElseIf dbReader.GetValue(0).ToString() = "Data table Name" AndAlso TableRow = False Then
                        If First = False Then
                            rt.ReferenceDatas = rds
                            rts.AddItem(rt)
                        End If
                        If First = True Then
                            First = False
                        End If
                        TableRow = True
                        ColRow = False
                    ElseIf ColRow = True Then
                        rd = New ReferenceDatas.ReferenceData
                        rd.ReferenceDataID = dbReader.GetValue(0).ToString()
                        rd.Description = dbReader.GetValue(1).ToString()
                        rd.MaxAmount = dbReader.GetValue(4).ToString()
                        Call getDatatype(dbReader.GetValue(3).ToString())
                        rd.Datatype = Datatype
                        rd.Datasize = Datasize
                        rd.Datascale = Datascale
                        If rd.Datatype = "NOT FOUND" OrElse rd.Datasize = "Err" Then
                            Throw New Exception("Data type in column " & rd.ReferenceDataID & " in reference table " & rt.ReferenceTypeID & " is not defined correctly. Please check documentation for further details.")
                        End If
                        rd.Nullable = dbReader.GetValue(6).ToString()
                        rd.DupConstraint = dbReader.GetValue(5).ToString()
                        rds.AddItem(rd)
                    ElseIf dbReader.GetValue(0).ToString() = "Column name" AndAlso TableRow = False Then
                        ColRow = True
                        rds = New ReferenceDatas
                    Else
                    End If
                End If
            End While

            dbReader.Close()
            dbCommand.Dispose()

            'Wksht = ExcelApp.Workbooks(1).Worksheets("TopologyData")
            'Rng = Wksht.Range("A:G")
            'RowCount = getRowCount(Rng, 12, 1)

            'For RowNum = 12 To RowCount
            'If TableRow = True Then
            'rt = New ReferenceTypes.ReferenceType
            'rt.ReferenceTypeID = Rng.Cells._Default(RowNum, 1).Value
            'rt.Description = Rng.Cells._Default(RowNum, 2).Value
            'rt.Type = Rng.Cells._Default(RowNum, 4).Value
            'rt.Update = Rng.Cells._Default(RowNum, 3).Value
            'rt.CurrentRequired = Rng.Cells._Default(RowNum, 5).Value
            'TableRow = False
            'ElseIf Rng.Cells._Default(RowNum, 1).Value = "Data table Name" AndAlso TableRow = False Then
            'TableRow = True
            'ColRow = False
            'ElseIf ColRow = True Then
            'rd = New ReferenceDatas.ReferenceData
            'rd.ReferenceDataID = Rng.Cells._Default(RowNum, 1).Value
            'rd.Description = Rng.Cells._Default(RowNum, 2).Value


            'Call getDatatype(Rng.Cells._Default(RowNum, 4).Value)
            'rd.Datatype = Datatype
            'rd.Datasize = Datasize
            'rd.Datascale = Datascale

            'If rd.Datatype = "NOT FOUND" OrElse rd.Datasize = "Err" Then
            'Throw New Exception("Row: " & RowNum2 & ". Data type in column " & rd.ReferenceDataID & " in reference table " & rt.ReferenceTypeID & " is not defined correctly. Please check documentation for further details.")
            'End If

            'rd.Nullable = Rng.Cells._Default(RowNum, 7).Value
            'rd.DupConstraint = Rng.Cells._Default(RowNum, 6).Value
            'rds.AddItem(rd)
            'If Rng.Cells._Default(RowNum + 1, 1).Value = "Data table Name" OrElse Rng.Cells._Default(RowNum + 1, 1).Value = "" Then
            'rt.ReferenceDatas = rds
            'rts.AddItem(rt)
            'ColRow = False
            'End If
            'ElseIf Rng.Cells._Default(RowNum, 1).Value = "Column name" AndAlso TableRow = False Then
            'ColRow = True
            'rds = New ReferenceDatas
            'Else
            'End If

            'Next RowNum



            Return 0


        Catch e As Exception
            MsgBox("Metadata Error: " & e.ToString())
            Return 1
        End Try



    End Function
    Public Function getRowCount(ByRef Rng As Excel.Range, ByRef StartRow As Integer, ByRef ColNumber As Integer) As Integer
        Dim RowCount As Integer
        Dim RowNum As Integer
        RowCount = StartRow
        For RowNum = StartRow To Rng.Rows.Count
            If Rng.Cells._Default(RowNum, ColNumber).Value <> "" Then
                RowCount += 1
            Else
                Exit For
            End If
        Next RowNum
        Return RowCount
    End Function
    Private Function Initialize_InterfaceClasses() As Integer

        Try

            dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Coversheet$C3:C4]", intfConn)
            dbReader = dbCommand.ExecuteReader()
            While (dbReader.Read())
                IntfSourcePack = dbReader.GetValue(0).ToString()
            End While
            dbReader.Close()
            dbCommand.Dispose()

            dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Coversheet$C4:C5]", intfConn)
            dbReader = dbCommand.ExecuteReader()
            While (dbReader.Read())
                IntfTPVersion = dbReader.GetValue(0).ToString()
            End While
            dbReader.Close()
            dbCommand.Dispose()

            dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Coversheet$C5:C6]", intfConn)
            dbReader = dbCommand.ExecuteReader()
            While (dbReader.Read())
                InterfaceList = Split(dbReader.GetValue(0).ToString(), ",")
            End While
            dbReader.Close()
            dbCommand.Dispose()

            'Wksht = ExcelApp.Workbooks(3).Worksheets("Coversheet")
            'Rng = Wksht.Range("A:C")

            'InterfaceList = Split(Rng.Cells._Default(6, 3).Value, ",")

            'IntfSourcePack = Rng.Cells._Default(4, 3).Value
            'IntfTPVersion = Rng.Cells._Default(5, 3).Value
            IntfPackRel = SourcePack & ":" & IntfTPVersion


            'Wksht = ExcelApp.Workbooks(3).Worksheets("Measurements")
            'Rng = Wksht.Range("A:C")
            'RowCount = getRowCount(Rng, 5, 1)

            'Data format information for measurements
            data_fmts = New DataFormats

            For IntfCount = 0 To UBound(InterfaceList)
                dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Measurements$A4:H60000]", intfConn)
                dbReader = dbCommand.ExecuteReader()
                While (dbReader.Read())
                    If dbReader.GetValue(0).ToString() = "" Then
                        Exit While
                    Else
                        data_fmt = New DataFormats.DataFormat

                        data_fmt.Measurement = dbReader.GetValue(0).ToString()
                        data_fmt.Release = dbReader.GetValue(1).ToString()
                        data_fmt.DataFormatType = InterfaceList(IntfCount)
                        data_fmt.TagId = dbReader.GetValue(IntfCount + 2).ToString()
                        data_fmt.Description = "Default tags for " & data_fmt.Measurement & " in " & IntfPackRel & " with format " & data_fmt.DataFormatType & "."
                        data_fmt.VersionId = IntfPackRel
                        data_fmt.DataFormatId = IntfPackRel & ":" & data_fmt.Measurement & ":" & data_fmt.DataFormatType
                        data_fmt.TypeId = IntfPackRel & ":" & data_fmt.Measurement
                        data_fmt.FolderName = data_fmt.Measurement
                        data_fmts.AddItem(data_fmt)
                    End If
                End While
                dbReader.Close()
                dbCommand.Dispose()

                'For RowNum = 5 To RowCount

                'If Rng.Cells._Default(RowNum, 1).Value <> "" Then

                'data_fmt = New DataFormats.DataFormat

                'data_fmt.Measurement = Rng.Cells._Default(RowNum, 1).Value
                'data_fmt.Release = Rng.Cells._Default(RowNum, 2).Value
                'data_fmt.DataFormatType = InterfaceList(IntfCount)
                'data_fmt.TagId = Rng.Cells._Default(RowNum, IntfCount + 3).Value
                'data_fmt.Description = "Default tags for " & data_fmt.Measurement & " in " & IntfPackRel & " with format " & data_fmt.DataFormatType & "."
                'data_fmt.VersionId = IntfPackRel
                'data_fmt.DataFormatId = IntfPackRel & ":" & data_fmt.Measurement & ":" & data_fmt.DataFormatType
                'data_fmt.TypeId = IntfPackRel & ":" & data_fmt.Measurement
                'data_fmt.FolderName = data_fmt.Measurement
                'data_fmts.AddItem(data_fmt)
                'End If
                'Next RowNum
            Next IntfCount

            'Data items for keys
            'Wksht = ExcelApp.Workbooks(3).Worksheets("Keys")
            'Rng = Wksht.Range("A:L")
            'RowCount = getRowCount(Rng, 5, 1)

            key_items = New DataItems
            For IntfCount = 0 To UBound(InterfaceList)
                dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Keys$A4:H60000]", intfConn)
                dbReader = dbCommand.ExecuteReader()
                While (dbReader.Read())
                    If dbReader.GetValue(0).ToString() = "" Then
                        Exit While
                    Else
                        key_item = New DataItems.DataItem

                        key_item.DataFormatId = IntfPackRel & ":" & dbReader.GetValue(0).ToString() & ":" & InterfaceList(IntfCount)
                        key_item.DataName = dbReader.GetValue(1).ToString()
                        key_item.Release = dbReader.GetValue(2).ToString()
                        key_item.DataId = dbReader.GetValue(IntfCount + 3).ToString()

                        key_items.AddItem(key_item)
                    End If
                End While
                dbReader.Close()
                dbCommand.Dispose()
                'For RowNum = 5 To RowCount
                'If Rng.Cells._Default(RowNum, 1).Value <> "" Then
                'key_item = New DataItems.DataItem

                'key_item.DataFormatId = IntfPackRel & ":" & Rng.Cells._Default(RowNum, 1).Value & ":" & InterfaceList(IntfCount)
                'key_item.DataName = Rng.Cells._Default(RowNum, 2).Value
                'key_item.Release = Rng.Cells._Default(RowNum, 3).Value
                'key_item.DataId = Rng.Cells._Default(RowNum, IntfCount + 4).Value

                'key_items.AddItem(key_item)
                'End If
                'Next RowNum
            Next IntfCount

            'Data items for counters
            'Wksht = ExcelApp.Workbooks(3).Worksheets("Counters")
            'Rng = Wksht.Range("A:L")
            'RowCount = getRowCount(Rng, 5, 1)

            data_items = New DataItems
            For IntfCount = 0 To UBound(InterfaceList)
                dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Counters$A4:H60000]", intfConn)
                dbReader = dbCommand.ExecuteReader()
                While (dbReader.Read())
                    If dbReader.GetValue(0).ToString() = "" Then
                        Exit While
                    Else
                        data_item = New DataItems.DataItem

                        data_item.DataFormatId = IntfPackRel & ":" & dbReader.GetValue(0).ToString() & ":" & InterfaceList(IntfCount)
                        data_item.DataName = dbReader.GetValue(1).ToString()
                        data_item.Release = dbReader.GetValue(2).ToString()
                        data_item.DataId = dbReader.GetValue(IntfCount + 3).ToString()

                        data_items.AddItem(data_item)
                    End If
                End While
                dbReader.Close()
                dbCommand.Dispose()
                'For RowNum = 5 To RowCount
                'If Rng.Cells._Default(RowNum, 1).Value <> "" Then
                'data_item = New DataItems.DataItem

                'data_item.DataFormatId = IntfPackRel & ":" & Rng.Cells._Default(RowNum, 1).Value & ":" & InterfaceList(IntfCount)
                'data_item.DataName = Rng.Cells._Default(RowNum, 2).Value
                'data_item.Release = Rng.Cells._Default(RowNum, 3).Value
                'data_item.DataId = Rng.Cells._Default(RowNum, IntfCount + 4).Value

                'data_items.AddItem(data_item)
                'End If
                'Next RowNum
            Next IntfCount



            'Data items for topology
            'Wksht = ExcelApp.Workbooks(3).Worksheets("Topology")
            'Rng = Wksht.Range("A:L")
            'RowCount = getRowCount(Rng, 5, 1)


            'Data format information for topology
            topology_fmts = New DataFormats

            For IntfCount = 0 To UBound(InterfaceList)

                For rt_count = 1 To rts.Count
                    rt = rts.Item(rt_count)
                    If rt.Type = "table" Then

                        topology_fmt = New DataFormats.DataFormat

                        topology_fmt.Measurement = rt.ReferenceTypeID
                        topology_fmt.Release = IntfTPVersion
                        topology_fmt.DataFormatType = InterfaceList(IntfCount)
                        topology_fmt.TagId = rt.ReferenceTypeID
                        topology_fmt.Description = "Default tags for " & topology_fmt.Measurement & " in " & IntfPackRel & " with format " & topology_fmt.DataFormatType & "."
                        topology_fmt.VersionId = IntfPackRel
                        topology_fmt.DataFormatId = IntfPackRel & ":" & topology_fmt.Measurement & ":" & topology_fmt.DataFormatType
                        topology_fmt.TypeId = IntfPackRel & ":" & topology_fmt.Measurement
                        topology_fmt.FolderName = topology_fmt.Measurement
                        topology_fmts.AddItem(topology_fmt)

                    End If
                Next rt_count
            Next IntfCount

            'Data items for topology
            topology_items = New DataItems
            For IntfCount = 0 To UBound(InterfaceList)
                dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Topology$A4:H60000]", intfConn)
                dbReader = dbCommand.ExecuteReader()
                While (dbReader.Read())
                    If dbReader.GetValue(0).ToString() = "" Then
                        Exit While
                    Else
                        topology_item = New DataItems.DataItem

                        topology_item.DataFormatId = IntfPackRel & ":" & dbReader.GetValue(0).ToString() & ":" & InterfaceList(IntfCount)
                        topology_item.DataName = dbReader.GetValue(1).ToString()
                        topology_item.Release = IntfTPVersion
                        topology_item.DataId = dbReader.GetValue(IntfCount + 2).ToString()

                        topology_items.AddItem(topology_item)
                    End If
                End While
                dbReader.Close()
                dbCommand.Dispose()
                'For RowNum = 5 To RowCount
                'If Rng.Cells._Default(RowNum, 1).Value <> "" Then

                'topology_item = New DataItems.DataItem

                'topology_item.DataFormatId = IntfPackRel & ":" & Rng.Cells._Default(RowNum, 1).Value & ":" & InterfaceList(IntfCount)
                'topology_item.DataName = Rng.Cells._Default(RowNum, 2).Value
                'topology_item.Release = IntfTPVersion
                'topology_item.DataId = Rng.Cells._Default(RowNum, IntfCount + 3).Value

                'topology_items.AddItem(topology_item)
                'End If
                'Next RowNum
            Next IntfCount
            Return 0
        Catch e As Exception
            MsgBox("Metadata Error: " & e.ToString())
            Return 1
        End Try

    End Function
    Sub TechPack_SampleData(ByVal FilePrefix As String)

        Dim iFileNum As Short
        Dim samplefile As String
        Dim sampledate As String
        Dim toDate As Date

        Dim sampleDays As Integer
        Dim i As Integer

        toDate = New Date(1999, 12, 6)

        sampleDays = 42

        For i = 0 To sampleDays - 1
            toDate = toDate.AddDays(1)
            For mt_count = 1 To mts.Count
                mt = mts.Item(mt_count)
                If toDate.Month < 10 Then
                    If toDate.Day < 10 Then
                        sampledate = toDate.Year & "-0" & toDate.Month & "-0" & toDate.Day
                    Else
                        sampledate = toDate.Year & "-0" & toDate.Month & "-" & toDate.Day
                    End If
                Else
                    If toDate.Day < 10 Then
                        sampledate = toDate.Year & "-" & toDate.Month & "-0" & toDate.Day
                    Else
                        sampledate = toDate.Year & "-" & toDate.Month & "-" & toDate.Day
                    End If
                End If

                samplefile = FilePrefix & mt.MeasurementTypeID & "_sample_" & sampledate & ".txt"
                iFileNum = FreeFile()
                FileOpen(iFileNum, samplefile, OpenMode.Output)
                mt.CreateSampleData(iFileNum, sampledate, toDate.Month, toDate.Day, i)
                FileClose(iFileNum)
            Next mt_count
        Next

    End Sub
End Class