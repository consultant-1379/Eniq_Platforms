Option Strict Off
Option Explicit On 

Imports System.Xml
Imports System.Xml.Xsl
Imports System
Imports System.IO

Public NotInheritable Class MetadataFunctions

    Dim Datatype As String
    Dim Datasize As String
    Dim Datascale As String
    Dim Description As String
    Dim VendorReleases As String

    Dim TPReleaseVersion As String
    Dim IntfTPReleaseVersion As String
    Dim VendorRelease As String
    Dim ProductNumber As String
    Dim PackRel As String
    Dim TechPackName As String
    Dim UseTechPackName As String
    Dim TechPackType As String
    Dim ExtStatementExecOrder As Integer
    Dim IntfTechPackType As String
    Dim UseIntfTechPackName As String
    Dim Interface_Version_Information As String

    Dim DefaultKeyMaxAmount As String
    Dim DefaultCounterMaxAmount As String

    Dim mts As MeasurementTypes
    Dim all_mts As MeasurementTypes
    Dim mt As MeasurementTypes.MeasurementType

    Dim rts As ReferenceTypes
    Dim rt As ReferenceTypes.ReferenceType
    Dim rds As ReferenceDatas
    Dim common_rds As ReferenceDatas
    Dim rd As ReferenceDatas.ReferenceData
    Dim common_rd As ReferenceDatas.ReferenceData
    Dim rt_count As Long
    Dim rd_count As Long

    Dim implementors As WebPortalImplementors
    Dim implementor As WebPortalImplementors.WebPortalImplementor

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
    Dim TPBuild As String

    Dim Version_Information As String

    'Interfaces

    Dim intfMeasurements As InterfaceMeasurements
    Dim intfMeasurement As InterfaceMeasurements.InterfaceMeasurement

    Dim data_intfs As DataInterfaces
    Dim data_intf As DataInterfaces.DataInterface

    Dim data_fmts As DataFormats
    Dim data_fmt As DataFormats.DataFormat
    Dim data_fmt_count As Long

    Dim topology_fmts As DataFormats
    Dim topology_fmt As DataFormats.DataFormat
    Dim topology_fmt_count As Long

    Dim MeasurementInterfaceList() As String
    Dim TopologyInterfaceList() As String
    Dim IntfTPVersion As String
    Dim IntfTechPackName As String
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

    Dim transforms As Transformations
    Dim transform As Transformations.Transformation


    ''
    '  Gets ReferenceData class from ReferenceDatas class based on given index.
    '
    ' @param Index Specifies the index in the ReferenceDatas class
    ' @return Reference to ReferenceData

    'Prints MeasurementTypeClass table metadata information.
    '
    'Input parameters: iFileNum - handler for metadata sql file
    'Input parameters: NewProgram - Support for program 5.0.2 metadata
    '
    'Uses MeasurementType class 
    Sub Metadata_Measurements(ByVal iFileNum As Short)

        Dim AddedClass As String
        AddedClass = ""

        PrintLine(iFileNum, "")

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If InStrRev(AddedClass, mt.MeasurementTypeClass & ",") = 0 Then
                PrintLine(iFileNum, "insert into MeasurementTypeClass " & _
                "(typeclassid, versionid, description) " & _
                "values (" & _
                "'" & PackRel & ":" & mt.MeasurementTypeClass & "'," & _
                "'" & PackRel & "'," & _
                "'" & mt.MeasurementTypeClassDescription & "');")
                AddedClass &= mt.MeasurementTypeClass & ","
            End If

            mt.PrintMeasurementInformation(iFileNum, PackRel, TechPackName, TPVersion, TechPackType)
        Next mt_count

        PrintLine(iFileNum, "")

    End Sub

    'Prints LOG_MonitoredTypes table information.
    '
    'Input parameters: iFileNum - handler for metadata sql file
    '
    'Uses MeasurementType class 
    Sub Metadata_MonitoredTypes(ByVal iFileNum As Short)
        Dim exec_order As Integer

        exec_order = 0

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)

            exec_order += 1
            PrintLine(iFileNum, "insert into ExternalStatement " & _
            "(versionid,statementname, executionorder, dbconnection, statement) " & _
            "values (" & _
            "'" & PackRel & "'," & _
            "'LOG_MonitoredTypes" & "_" & exec_order & "'," & _
            exec_order & "," & _
            "'dwh'," & _
            "'" & "insert into LOG_MonitoredTypes " & _
            "(TECHPACK_NAME, TYPENAME, TIMELEVEL, STATUS, MODIFIED, ACTIVATIONDAY) " & _
            "values (" & _
            "'" & TechPackName & "'," & _
            "'" & mt.MeasurementTypeID & "'," & _
            "'HOUR'," & _
            "'ACTIVE'," & _
            "now()," & _
            "today());" & "');")
        Next mt_count

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
        Dim actual_table As String
        Dim PrevTable As String
        Dim common_count As Integer
        Dim AddedTables As String


        For rt_count = 1 To rts.Count
            rt = rts.Item(rt_count)
            If rt.Type = "table" Then
                ExtStatementExecOrder = PringTopologyTable(iFileNum, ExtStatementExecOrder, rt)
            End If
        Next rt_count

        AddedTables = ""

        For rt_count = 1 To rts.Count
            rt = rts.Item(rt_count)
            Colnumber = 0
            For rd_count = 1 To rds.Count
                rd = rds.Item(rd_count)
                If rt.ReferenceTypeID = rd.ReferenceTypeID Then
                    Colnumber += 1
                    PrintTopologyColumn(iFileNum, Colnumber, rd)
                End If
            Next rd_count
        Next rt_count

        PrintLine(iFileNum, "")

    End Sub
    Sub PrintTopologyColumn(ByVal iFileNum As Short, ByRef Colnumber As Integer, ByVal rd As ReferenceDatas.ReferenceData)

        Dim ColIndex As String
        Dim CurrentRequired As Boolean

        If rd.Datasize > 255 Then 'OrElse rd.DupConstraint = 1 
            ColIndex = ""
        Else
            ColIndex = "HG"
        End If

        PrintLine(iFileNum, "insert into ReferenceColumn " & _
        "(typeid, colnumber, dataname, datatype, datasize, datascale, uniquevalue, nullable, indexes, uniquekey, includesql, includeupd) " & _
        "values (" & _
        "'" & PackRel & ":" & rd.ReferenceTypeID & "'," & _
        "" & Colnumber & "," & _
        "'" & rd.ReferenceDataID & "'," & _
        "'" & rd.Datatype & "'," & _
        rd.Datasize & "," & _
        rd.Datascale & "," & _
        rd.MaxAmount & "," & _
        rd.Nullable & "," & _
        "'" & ColIndex & "'," & _
        rd.DupConstraint & "," & _
        rd.IncludeInSQLInterface & "," & _
        rd.IncludeInTopologyUpdate & ");")

        For rt_count = 1 To rts.Count
            rt = rts.Item(rt_count)
            If rt.ReferenceTypeID = rd.ReferenceTypeID Then
                CurrentRequired = rt.CurrentRequired
                Exit For
            End If
        Next rt_count

        'CURRENT tables are not included in SQL interface or Topology Update
        If CurrentRequired = True Then
            PrintLine(iFileNum, "insert into ReferenceColumn " & _
            "(typeid, colnumber, dataname, datatype, datasize, datascale, uniquevalue, nullable, indexes, uniquekey, includesql, includeupd) " & _
            "values (" & _
            "'" & PackRel & ":" & rd.ReferenceTypeID & "_CURRENT_DC'," & _
            "" & Colnumber & "," & _
            "'" & rd.ReferenceDataID & "'," & _
            "'" & rd.Datatype & "'," & _
            rd.Datasize & "," & _
            rd.Datascale & "," & _
            rd.MaxAmount & "," & _
            rd.Nullable & "," & _
            "'" & ColIndex & "'," & _
            rd.DupConstraint & "," & _
            "0," & _
            "0);")
        End If

    End Sub
    Function PringTopologyTable(ByVal iFileNum As Short, ByRef exec_order As Integer, ByVal rt As ReferenceTypes.ReferenceType)

        Dim viewSql As String
        PrintLine(iFileNum, "insert into ReferenceTable " & _
        "(typeid, typename, versionid, description, objectid, objectname, update_policy) " & _
        "values (" & _
        "'" & PackRel & ":" & rt.ReferenceTypeID & "'," & _
        "'" & rt.ReferenceTypeID & "'," & _
        "'" & PackRel & "'," & _
        "'" & rt.Description & "'," & _
        "'" & PackRel & ":" & rt.ReferenceTypeID & "'," & _
        "'" & rt.ReferenceTypeID & "'," & _
        rt.Update & ");")

        If rt.CurrentRequired = True Then
            PrintLine(iFileNum, "insert into ReferenceTable " & _
            "(typeid, typename, versionid, description, objectid, objectname, update_policy) " & _
            "values (" & _
            "'" & PackRel & ":" & rt.ReferenceTypeID & "_CURRENT_DC'," & _
            "'" & rt.ReferenceTypeID & "_CURRENT_DC'," & _
            "'" & PackRel & "'," & _
            "'" & rt.Description & "'," & _
            "'" & PackRel & ":" & rt.ReferenceTypeID & "_CURRENT_DC'," & _
            "'" & rt.ReferenceTypeID & "_CURRENT_DC'," & _
            "0);")

            viewSql = "IF (SELECT count(*) FROM sys.sysviews where viewname = '" & rt.ReferenceTypeID & "_CURRENT') > 0 "
            viewSql &= " DROP VIEW " & rt.ReferenceTypeID & "_CURRENT "
            'viewSql &= " ELSE "
            viewSql &= " BEGIN "
            viewSql &= "CREATE VIEW " & rt.ReferenceTypeID & "_CURRENT as select * from " & rt.ReferenceTypeID & "_CURRENT_DC "
            viewSql &= " END "

            viewSql = Replace(viewSql, "'", "''")

            exec_order += 1
            PrintLine(iFileNum, "insert into ExternalStatement " & _
            "(versionid,statementname, executionorder, dbconnection, statement) " & _
            "values (" & _
            "'" & PackRel & "'," & _
            "'create view " & rt.ReferenceTypeID & "_CURRENT" & "'," & _
            exec_order & "," & _
            "'dwh'," & _
            "'" & viewSql & "');")
        End If
        Return exec_order
    End Function


    Sub Interface_InterfaceMeasurements(ByVal iFileNum As Short)

        Dim count As Integer

        For count = 1 To intfMeasurements.Count
            intfMeasurement = intfMeasurements.Item(count)
            PrintLine(iFileNum, "insert into InterfaceMeasurement " & _
            "(tagid,dataformatid,interfacename,transformerid,status,modiftime,description) " & _
            "values (" & _
            "'" & intfMeasurement.TagId & "'," & _
            "'" & intfMeasurement.DataFormatId & "'," & _
            "'" & intfMeasurement.InterfaceName & "'," & _
            "'" & intfMeasurement.TransformerId & "'," & _
            intfMeasurement.Status & "," & _
            "now()," & _
            "'" & intfMeasurement.Description & "');")
        Next count

        PrintLine(iFileNum, "")

    End Sub

    Sub Interface_DataInterfaces(ByVal iFileNum As Short)

        Dim count As Integer

        For count = 1 To data_intfs.Count
            data_intf = data_intfs.Item(count)
            PrintLine(iFileNum, "insert into DataInterface " & _
            "(interfacename,status,interfacetype,description) " & _
            "values (" & _
            "'" & data_intf.InterfaceName & "'," & _
            data_intf.Status & "," & _
            "'" & data_intf.InterfaceType & "'," & _
            "'" & data_intf.Description & "');")
        Next count

        PrintLine(iFileNum, "")

    End Sub

    Sub Interface_DefaultTags(ByVal iFileNum As Short)

        Dim count As Integer

        For count = 1 To data_fmts.Count
            data_fmt = data_fmts.Item(count)
            PrintLine(iFileNum, "insert into DefaultTags " & _
            "(tagid, dataformatid, description) " & _
            "values (" & _
            "'" & data_fmt.TagId & "'," & _
            "'" & data_fmt.DataFormatId & "'," & _
            "'" & data_fmt.Description & "');")
        Next count

        For count = 1 To topology_fmts.Count
            topology_fmt = topology_fmts.Item(count)
            PrintLine(iFileNum, "insert into DefaultTags " & _
            "(tagid, dataformatid, description) " & _
            "values (" & _
            "'" & topology_fmt.TagId & "'," & _
            "'" & topology_fmt.DataFormatId & "'," & _
            "'" & topology_fmt.Description & "');")
        Next count

        PrintLine(iFileNum, "")

    End Sub
    Sub Interface_DataFormat(ByVal iFileNum As Short)

        Dim PreviousDataFormatId As String
        Dim count As Integer

        PreviousDataFormatId = ""
        For count = 1 To data_fmts.Count
            data_fmt = data_fmts.Item(count)
            If data_fmt.DataFormatId <> PreviousDataFormatId Then
                PrintLine(iFileNum, "insert into DataFormat " & _
                "( dataformatid, typeid, versionid, objecttype, foldername, dataformattype) " & _
                "values (" & _
                "'" & data_fmt.DataFormatId & "'," & _
                "'" & data_fmt.TypeId & "'," & _
                "'" & data_fmt.VersionId & "'," & _
                "'Measurement'," & _
                "'" & data_fmt.FolderName & "'," & _
                "'" & data_fmt.DataFormatType & "');")
            End If
            PreviousDataFormatId = data_fmt.DataFormatId
        Next count

        PreviousDataFormatId = ""
        For count = 1 To topology_fmts.Count
            topology_fmt = topology_fmts.Item(count)
            If topology_fmt.DataFormatId <> PreviousDataFormatId Then
                PrintLine(iFileNum, "insert into DataFormat " & _
                "( dataformatid, typeid, versionid, objecttype, foldername,dataformattype) " & _
                "values (" & _
                "'" & topology_fmt.DataFormatId & "'," & _
                "'" & topology_fmt.TypeId & "'," & _
                "'" & topology_fmt.VersionId & "'," & _
                "'Reference'," & _
                "'" & topology_fmt.FolderName & "'," & _
                "'" & topology_fmt.DataFormatType & "');")
            End If
            PreviousDataFormatId = topology_fmt.DataFormatId
        Next count
        PrintLine(iFileNum, "")

    End Sub
    Sub Interface_DataItem(ByVal iFileNum As Short)

        Dim Colnumber As Long
        Dim PreviousDataFormatId As String

        Dim count As Integer
        Dim item_count As Integer
        Dim rel_key_cnt As Integer
        Dim RelFound As Boolean

        For count = 1 To data_fmts.Count
            data_fmt = data_fmts.Item(count)
            If data_fmt.DataFormatId <> PreviousDataFormatId Then

                'Keys
                Colnumber = 0
                For item_count = 1 To key_items.Count
                    key_item = key_items.Item(item_count)
                    If key_item.DataFormatId = data_fmt.DataFormatId Then
                        Colnumber += 1
                        PrintLine(iFileNum, "insert into DataItem " & _
                        "(dataformatid, dataname, colnumber, dataid, process_instruction) " & _
                        "values (" & _
                        "'" & key_item.DataFormatId & "'," & _
                        "'" & key_item.DataName & "'," & _
                        Colnumber & "," & _
                        "'" & key_item.DataId & "'," & _
                        "'" & key_item.ProcessInstruction & "');")
                    End If
                Next item_count

                'Public Keys
                Colnumber = 50
                For item_count = 1 To pub_keys.Count
                    pub_key = pub_keys.Item(item_count)
                    If pub_key.KeyType = "RAW" Then
                        Colnumber += 1
                        PrintLine(iFileNum, "insert into DataItem " & _
                        "(dataformatid, dataname, colnumber, dataid, process_instruction) " & _
                        "values (" & _
                        "'" & data_fmt.DataFormatId & "'," & _
                        "'" & pub_key.PublicKeyName & "'," & _
                        Colnumber & "," & _
                        "'" & pub_key.PublicKeyName & "'," & _
                        "'" & "');")
                    End If
                Next item_count

                'Counters
                Colnumber = 100
                For item_count = 1 To data_items.Count
                    data_item = data_items.Item(item_count)
                    If data_item.DataFormatId = data_fmt.DataFormatId Then
                        Colnumber += 1
                        PrintLine(iFileNum, "insert into DataItem " & _
                        "(dataformatid, dataname, colnumber, dataid, process_instruction) " & _
                        "values (" & _
                        "'" & data_item.DataFormatId & "'," & _
                        "'" & data_item.DataName & "'," & _
                        Colnumber & "," & _
                        "'" & data_item.DataId & "'," & _
                        "'" & data_item.ProcessInstruction & "');")
                    End If
                Next item_count

            End If
            PreviousDataFormatId = data_fmt.DataFormatId
        Next count

        PreviousDataFormatId = ""
        For count = 1 To topology_fmts.Count
            topology_fmt = topology_fmts.Item(count)
            If topology_fmt.DataFormatId <> PreviousDataFormatId Then
                'Counters
                Colnumber = 0
                For item_count = 1 To topology_items.Count
                    topology_item = topology_items.Item(item_count)
                    If topology_item.DataFormatId = topology_fmt.DataFormatId Then
                        Colnumber += 1
                        PrintLine(iFileNum, "insert into DataItem " & _
                        "(dataformatid, dataname, colnumber, dataid, process_instruction) " & _
                        "values (" & _
                        "'" & topology_item.DataFormatId & "'," & _
                        "'" & topology_item.DataName & "'," & _
                        Colnumber & "," & _
                        "'" & topology_item.DataId & "'," & _
                        "'" & topology_item.ProcessInstruction & "');")
                    End If
                Next item_count
            End If
            PreviousDataFormatId = topology_fmt.DataFormatId
        Next count

        PrintLine(iFileNum, "")

    End Sub

    Sub Interface_Transformation(ByVal iFileNum As Short)

        Dim count As Integer
        Dim orderno As Integer
        Dim AddedClass As String
        AddedClass = ""

        If Not transforms Is Nothing Then
            For count = 1 To transforms.Count
                transform = transforms.Item(count)
                If InStrRev(AddedClass, transform.TransformationId & ",") = 0 Then
                    PrintLine(iFileNum, "insert into Transformer " & _
                    "(transformerid, versionid) " & _
                    "values (" & _
                    "'" & transform.TransformationId & "'," & _
                    "'" & transform.Release & "');")
                    AddedClass &= transform.TransformationId & ","
                End If
            Next count

            For count = 1 To transforms.Count
                transform = transforms.Item(count)
                PrintLine(iFileNum, "insert into Transformation " & _
                "(transformerid, orderno, type, source, target, config) " & _
                "values (" & _
                "'" & transform.TransformationId & "'," & _
                count & "," & _
                "'" & transform.Type & "'," & _
                "'" & transform.Source & "'," & _
                "'" & transform.Target & "'," & _
                "'" & Replace(transform.Config, "'", "''") & "');")
            Next count
        End If

        PrintLine(iFileNum, "")

    End Sub

    Sub Webportal_Configuration(ByVal iFileNum As Short)

        Dim count As Integer
        Dim option_count As Integer
        Dim prompt_count As Integer
        Dim id_count As Integer
        Dim implementorOption As WebPortalImplementorOptions.WebPortalImplementorOption
        Dim implementorPrompt As WebPortalPrompts.WebPortalPrompt

        id_count = 0

        For count = 1 To implementors.Count
            implementor = implementors.Item(count)
            id_count += 1
            PrintLine(iFileNum, "insert into PromptImplementor " & _
            "(versionid,promptimplementorid,promptclassname,priority) " & _
            "values (" & _
            "'" & PackRel & "'," & _
            id_count & "," & _
            "'" & implementor.ClassName & "'," & _
            implementor.Priority & ");")

            For option_count = 1 To implementor.ImplementorOptions.Count
                implementorOption = implementor.ImplementorOptions.Item(option_count)
                PrintLine(iFileNum, "insert into PromptOption " & _
                "(versionid,promptimplementorid,optionname,optionvalue) " & _
                "values (" & _
                "'" & PackRel & "'," & _
                id_count & "," & _
                "'" & implementorOption.OptionName & "'," & _
                "'" & implementorOption.OptionValue & "');")
                implementorOption = Nothing
            Next option_count

            For prompt_count = 1 To implementor.ImplementorPrompts.Count
                implementorPrompt = implementor.ImplementorPrompts.Item(prompt_count)
                PrintLine(iFileNum, "insert into Prompt " & _
                "(versionid,promptimplementorid,promptname,ordernumber,unrefreshable) " & _
                "values (" & _
                "'" & PackRel & "'," & _
                id_count & "," & _
                "'^" & implementorPrompt.PromptText & "'," & _
                implementorPrompt.Order & "," & _
                "'" & implementorPrompt.Refreshable & "');")
                implementorPrompt = Nothing
            Next prompt_count
            implementor = Nothing
        Next count

        PrintLine(iFileNum, "")
    End Sub


    Function MakeTPDescription(ByRef Filename As String, ByRef BaseFilename As String, ByRef IntfFilename As String, ByRef OutputDir_Original As String, ByRef CustomInterface As Boolean) As logMessages


        Dim metafile As String
        Dim iFileNum As Short
        Dim logs As logMessages
        Dim message As logMessages.Message
        Dim count As Integer
        Dim OutputDir As String

        'update build number
        'Dim tp_excel = New TPExcelWriter
        'Dim updateBuild = tp_excel.updateBuildNumber(Filename, "metadata", OutputDir_Original)
        'tp_excel = Nothing

        tpAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & Filename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;ReadOnly=0;IMEX=1"""
        baseAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & BaseFilename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;ReadOnly=0;IMEX=1"""
        intfAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & IntfFilename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;ReadOnly=0;IMEX=1"""

        tpConn = New System.Data.OleDb.OleDbConnection(tpAdoConn)
        baseConn = New System.Data.OleDb.OleDbConnection(baseAdoConn)
        intfConn = New System.Data.OleDb.OleDbConnection(intfAdoConn)

        tpConn.Open()
        baseConn.Open()
        intfConn.Open()

        logs = New logMessages


        Dim ClsInit As Boolean
        ExtStatementExecOrder = 0

        ClsInit = Initialize_Classes(logs)

        If ClsInit = False Then
            tpConn.Close()
            baseConn.Close()
            intfConn.Close()
            MsgBox("Run complete.")
        Else
            Initialize_InterfaceClasses(logs)

            OutputDir = OutputDir_Original & "\doc"
            Try
                If Not System.IO.Directory.Exists(OutputDir) Then
                    System.IO.Directory.CreateDirectory(OutputDir)
                End If
            Catch ex As Exception
                logs.AddLogText("Create Directory '" & OutputDir & "' failed: " & ex.Message & " " & ex.StackTrace)
            End Try

            Dim OutputXMLWriter As New XmlTextWriter(OutputDir & "\temp.xml", System.Text.Encoding.UTF8)
            OutputXMLWriter.Formatting = Formatting.Indented

            OutputXMLWriter.WriteStartDocument()
            OutputXMLWriter.WriteStartElement("document")

            'xml content
            DocTPDescription(OutputXMLWriter)
            DocFactTables(OutputXMLWriter)
            DocBusyHours(OutputXMLWriter)
            DocTopologyTables(OutputXMLWriter)
            DocInterfaces(OutputXMLWriter)
            DocSQLInterface(OutputXMLWriter)

            OutputXMLWriter.WriteEndElement()
            System.Threading.Thread.Sleep(1000) ' Sleep for 1 second

            OutputXMLWriter.WriteEndDocument()
            OutputXMLWriter.Flush()
            OutputXMLWriter.Close()

            Dim xslt As New XslTransform

            Try
                xslt.Load(Application.StartupPath() & "\TP_Description_SDIF.xslt")
                xslt.Transform(OutputDir & "\temp.xml", OutputDir & "\TP Description " & Description & ".sdif", Nothing)
                xslt.Load(Application.StartupPath() & "\TP_Description.xslt")
                xslt.Transform(OutputDir & "\temp.xml", OutputDir & "\TP Description " & Description & ".html", Nothing)
            Catch ex As Exception
                logs.AddLogText("Transform error: " & ex.Message & " " & ex.StackTrace)
            End Try

            Try
                If System.IO.File.Exists(OutputDir & "\temp.xml") Then
                    'System.IO.File.Delete(OutputDir & "\temp.xml")
                End If
            Catch ex As Exception
                MsgBox("Removing temporary file '" & OutputDir & "\temp.xml" & "' failed: " & ex.Message & " " & ex.StackTrace)
            End Try

            fixSDIFoutput(OutputDir & "\TP Description " & Description & ".sdif")

            tpConn.Close()
            baseConn.Close()
            intfConn.Close()

            MsgBox("Run complete.")

        End If
        Return logs


    End Function
    Private Sub fixSDIFoutput(ByVal SDIFFile As String)
        Dim lines() As String = {}

        If File.Exists(SDIFFile) Then
            Dim sr As New StreamReader(SDIFFile)
            Dim fileText As String = sr.ReadToEnd()
            sr.Close()

            lines = Split(fileText, vbCrLf)

            Dim fs As New FileStream(SDIFFile, FileMode.Create)
            Dim sw As New StreamWriter(fs)

            Dim original As String = "?>"
            Dim replacement As String = "?><!DOCTYPE doc PUBLIC " & Chr(34) & "-//ERICSSON//DTD XSEIF 1/FAD 110 05 R5//EN" & Chr(34) & " " & Chr(34) & "XSEIF_R5.dtd" & Chr(34) & ">"

            Dim foundBlank As Boolean
            For Each line As String In lines
                Replace(line, original, replacement)
                sw.WriteLine(line)
            Next
            sw.Close()
            fs.Close()
        End If
    End Sub
    Private Sub DocTPDescription(ByVal OutputXMLWriter As XmlTextWriter)
        '<tp_description name="Ericsson ICT PM" vendor_releases="P5,P6" product="CXC 12345 R2B" />
        OutputXMLWriter.WriteStartElement("tp_description")
        OutputXMLWriter.WriteAttributeString("name", Description)
        OutputXMLWriter.WriteAttributeString("vendor_releases", VendorReleases)
        OutputXMLWriter.WriteAttributeString("product", ProductNumber)
        OutputXMLWriter.WriteAttributeString("release", TPReleaseVersion)
        OutputXMLWriter.WriteEndElement()
    End Sub
    Private Sub DocFactTables(ByVal OutputXMLWriter As XmlTextWriter)

        Dim count As Integer
        Dim key As CounterKeys.CounterKey
        Dim cnt As Counters.Counter

        OutputXMLWriter.WriteStartElement("facts")
        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            OutputXMLWriter.WriteStartElement("fact")
            OutputXMLWriter.WriteAttributeString("name", mt.MeasurementTypeID)
            OutputXMLWriter.WriteAttributeString("size", mt.PartitionPlan)
            If mt.DayAggregation = True Then
                OutputXMLWriter.WriteAttributeString("aggregation", "Total")
            Else
                OutputXMLWriter.WriteAttributeString("aggregation", "None")
            End If
            If mt.ObjectBusyHours <> "" Then
                OutputXMLWriter.WriteAttributeString("obj_bh_support", mt.ObjectBusyHours)
            Else
                OutputXMLWriter.WriteAttributeString("obj_bh_support", "None")
            End If
            If mt.ElementBusyHours = True Then
                OutputXMLWriter.WriteAttributeString("elem_bh_support", "Yes")
            Else
                OutputXMLWriter.WriteAttributeString("elem_bh_support", "No")
            End If
            If mt.CreateCountTable = True Then
                If mt.CountSupport = "X" OrElse mt.CountSupport = "YES" Then
                    OutputXMLWriter.WriteAttributeString("delta_support", "Yes")
                Else
                    OutputXMLWriter.WriteAttributeString("delta_support", mt.CountSupport)
                End If
            Else
                OutputXMLWriter.WriteAttributeString("delta_support", "No")
            End If

            If mt.RankTable = True Then
                OutputXMLWriter.WriteAttributeString("rank_table", mt.RankTable)
            Else
                OutputXMLWriter.WriteAttributeString("fact_table", mt.RankTable)
            End If

            For count = 1 To mt.CounterKeys.Count
                key = mt.CounterKeys.Item(count)
                OutputXMLWriter.WriteStartElement("key")
                OutputXMLWriter.WriteAttributeString("name", key.CounterKeyName)
                OutputXMLWriter.WriteAttributeString("data_type", key.givenDatatype)
                If key.DupConstraint = 1 Then
                    OutputXMLWriter.WriteAttributeString("duplicate_constraint", "Yes")
                Else
                    OutputXMLWriter.WriteAttributeString("duplicate_constraint", "No")
                End If
                OutputXMLWriter.WriteEndElement()
            Next count
            For count = 1 To mt.Counters.Count
                cnt = mt.Counters.Item(count)
                OutputXMLWriter.WriteStartElement("counter")
                OutputXMLWriter.WriteAttributeString("name", cnt.CounterName)
                OutputXMLWriter.WriteAttributeString("data_type", cnt.givenDatatype)
                OutputXMLWriter.WriteAttributeString("time_aggregation", cnt.TimeAggr)
                OutputXMLWriter.WriteAttributeString("group_aggregation", cnt.GroupAggr)
                OutputXMLWriter.WriteAttributeString("type", cnt.CounterType)
                OutputXMLWriter.WriteEndElement()
            Next count

            OutputXMLWriter.WriteEndElement()
        Next mt_count
        OutputXMLWriter.WriteEndElement()
    End Sub
    Private Sub DocTopologyTables(ByVal OutputXMLWriter As XmlTextWriter)

        Dim count As Integer
        Dim key As CounterKeys.CounterKey
        Dim cnt As Counters.Counter

        OutputXMLWriter.WriteStartElement("dimensions")
        For rt_count = 1 To rts.Count
            rt = rts.Item(rt_count)
            OutputXMLWriter.WriteStartElement("dimension")
            OutputXMLWriter.WriteAttributeString("name", rt.ReferenceTypeID)
            OutputXMLWriter.WriteAttributeString("type", rt.Type)
            If rt.Update = 2 Then
                OutputXMLWriter.WriteAttributeString("update", "dynamic")
            ElseIf rt.Update = 1 Then
                OutputXMLWriter.WriteAttributeString("update", "predefined")
            Else
                OutputXMLWriter.WriteAttributeString("update", "static")
            End If


            For rd_count = 1 To rds.Count
                rd = rds.Item(rd_count)
                If rt.ReferenceTypeID = rd.ReferenceTypeID Then
                    OutputXMLWriter.WriteStartElement("column")
                    OutputXMLWriter.WriteAttributeString("name", rd.ReferenceDataID)
                    OutputXMLWriter.WriteAttributeString("data_type", rd.givenDatatype)
                    If rd.DupConstraint = 1 Then
                        OutputXMLWriter.WriteAttributeString("duplicate_constraint", "Yes")
                    Else
                        OutputXMLWriter.WriteAttributeString("duplicate_constraint", "No")
                    End If
                    If rd.IncludeInTopologyUpdate = 1 Then
                        OutputXMLWriter.WriteAttributeString("included_update", "Yes")
                    Else
                        OutputXMLWriter.WriteAttributeString("included_update", "No")
                    End If
                    OutputXMLWriter.WriteEndElement()
                End If
            Next rd_count
            OutputXMLWriter.WriteEndElement()
        Next rt_count
        OutputXMLWriter.WriteEndElement()
    End Sub
    Private Sub DocInterfaces(ByVal OutputXMLWriter As XmlTextWriter)

        Dim count As Integer
        Dim key As CounterKeys.CounterKey
        Dim cnt As Counters.Counter

        OutputXMLWriter.WriteStartElement("interfaces")
        For count = 1 To data_intfs.Count
            data_intf = data_intfs.Item(count)
            If data_intf.InterfaceType <> "" Then
                OutputXMLWriter.WriteStartElement("interface")
                OutputXMLWriter.WriteAttributeString("name", "INTF_" & IntfTechPackName & "_" & data_intf.InterfaceType)
                OutputXMLWriter.WriteAttributeString("type", data_intf.InterfaceType)
                OutputXMLWriter.WriteEndElement()
            End If
        Next count
        OutputXMLWriter.WriteEndElement()
    End Sub
    Private Sub DocSQLInterface(ByVal OutputXMLWriter As XmlTextWriter)


        OutputXMLWriter.WriteStartElement("sql_interface")
        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.RankTable = False Then
                If mt.PlainTable = False Then
                    OutputXMLWriter.WriteStartElement("view")
                    OutputXMLWriter.WriteAttributeString("name", mt.MeasurementTypeID & "_RAW")
                    getSQLInterfaceColumns(mt, "RAW", OutputXMLWriter)
                    OutputXMLWriter.WriteEndElement()
                End If
                If mt.DayAggregation = True Then
                    OutputXMLWriter.WriteStartElement("view")
                    OutputXMLWriter.WriteAttributeString("name", mt.MeasurementTypeID & "_DAY")
                    getSQLInterfaceColumns(mt, "DAY", OutputXMLWriter)
                    OutputXMLWriter.WriteEndElement()
                End If
                If mt.ObjectBusyHours <> "" Then
                    OutputXMLWriter.WriteStartElement("view")
                    OutputXMLWriter.WriteAttributeString("name", mt.MeasurementTypeID & "_DAYBH")
                    getSQLInterfaceColumns(mt, "DAYBH", OutputXMLWriter)
                    OutputXMLWriter.WriteEndElement()
                End If
                If mt.CreateCountTable = True Then
                    OutputXMLWriter.WriteStartElement("view")
                    OutputXMLWriter.WriteAttributeString("name", mt.MeasurementTypeID & "_COUNT")
                    getSQLInterfaceColumns(mt, "COUNT", OutputXMLWriter)
                    OutputXMLWriter.WriteEndElement()
                End If
                If mt.PlainTable = True Then
                    OutputXMLWriter.WriteStartElement("view")
                    OutputXMLWriter.WriteAttributeString("name", mt.MeasurementTypeID)
                    getSQLInterfaceColumns(mt, "PLAIN", OutputXMLWriter)
                    OutputXMLWriter.WriteEndElement()
                End If
            Else
                OutputXMLWriter.WriteStartElement("view")
                OutputXMLWriter.WriteAttributeString("name", mt.MeasurementTypeID & "_RANKBH")
                getSQLInterfaceColumns(mt, "RANKBH", OutputXMLWriter)
                OutputXMLWriter.WriteEndElement()
            End If
        Next mt_count
        Dim ViewColumns As String
        For rt_count = 1 To rts.Count
            rt = rts.Item(rt_count)
            OutputXMLWriter.WriteStartElement("view")
            OutputXMLWriter.WriteAttributeString("name", rt.ReferenceTypeID)
            getSQLInterfaceColumns(rt, OutputXMLWriter)
            OutputXMLWriter.WriteEndElement()
        Next rt_count
        OutputXMLWriter.WriteEndElement()
    End Sub
    Sub getSQLInterfaceColumns(ByRef rt As ReferenceTypes.ReferenceType, ByVal OutputXMLWriter As XmlTextWriter)

        Dim count As Integer
        Dim ViewColumns As String
        Dim first As Boolean

        OutputXMLWriter.WriteStartElement("columns")

        For rd_count = 1 To rds.Count
            rd = rds.Item(rd_count)
            If rt.ReferenceTypeID = rd.ReferenceTypeID Then
                If rd.IncludeInSQLInterface = 1 Then
                    OutputXMLWriter.WriteStartElement("column")
                    OutputXMLWriter.WriteAttributeString("name", rd.ReferenceDataID)
                    OutputXMLWriter.WriteAttributeString("datatype", rd.givenDatatype)
                    OutputXMLWriter.WriteEndElement()
                End If
            End If
        Next rd_count
        OutputXMLWriter.WriteEndElement()
    End Sub
    Sub getSQLInterfaceColumns(ByRef mt As MeasurementTypes.MeasurementType, ByRef TableLevel As String, ByVal OutputXMLWriter As XmlTextWriter)

        Dim count As Integer
        Dim ViewColumns As String
        Dim first As Boolean
        Dim key As CounterKeys.CounterKey
        Dim pubkey As PublicKeys.PublicKey
        Dim cnt As Counters.Counter

        OutputXMLWriter.WriteStartElement("columns")

        For count = 1 To mt.CounterKeys.Count
            key = mt.CounterKeys.Item(count)
            If key.IncludeInSQLInterface = 1 Then
                OutputXMLWriter.WriteStartElement("column")
                OutputXMLWriter.WriteAttributeString("name", key.CounterKeyName)
                OutputXMLWriter.WriteAttributeString("datatype", key.givenDatatype)
                OutputXMLWriter.WriteEndElement()
            End If
        Next count
        For count = 1 To mt.PublicKeys.Count
            pubkey = mt.PublicKeys.Item(count)
            If pubkey.IncludeInSQLInterface = 1 And pubkey.KeyType = TableLevel Then
                OutputXMLWriter.WriteStartElement("column")
                OutputXMLWriter.WriteAttributeString("name", pubkey.PublicKeyName)
                OutputXMLWriter.WriteAttributeString("datatype", pubkey.givenDatatype)
                OutputXMLWriter.WriteEndElement()
            End If
        Next count
        For count = 1 To mt.Counters.Count
            cnt = mt.Counters.Item(count)
            If cnt.IncludeInSQLInterface = 1 Then
                OutputXMLWriter.WriteStartElement("column")
                OutputXMLWriter.WriteAttributeString("name", cnt.CounterName)
                OutputXMLWriter.WriteAttributeString("datatype", cnt.givenDatatype)
                OutputXMLWriter.WriteEndElement()
            End If
        Next count
        OutputXMLWriter.WriteEndElement()

    End Sub
    Function DocBusyHours(ByVal OutputXMLWriter As XmlTextWriter)

        Dim count As Integer
        Dim object_count As Integer
        Dim ranking As String
        Dim BHObjects() As String

        OutputXMLWriter.WriteStartElement("busyhours")
        'objects
        For count = 1 To all_bh_types.Count
            'MULTIPLE object bhs
            bh_type = all_bh_types.Item(count)
            BHObjects = Split(bh_type.BHObjects, ",")
            For object_count = 0 To UBound(BHObjects)
                If BHObjects(object_count) <> "" Then
                    ranking = BHObjects(object_count) & "_" & bh_type.BHType
                    OutputXMLWriter.WriteStartElement("busyhour")
                    OutputXMLWriter.WriteAttributeString("name", ranking)
                    OutputXMLWriter.WriteAttributeString("description", BHObjects(object_count) & " " & bh_type.Description)
                    OutputXMLWriter.WriteAttributeString("criteria", bh_type.BHCriteria)
                    OutputXMLWriter.WriteAttributeString("source", bh_type.BHSource)
                    OutputXMLWriter.WriteAttributeString("where", bh_type.BHWhere)
                    OutputXMLWriter.WriteEndElement()
                End If
            Next object_count
        Next count

        'elements
        For count = 1 To all_bh_types.Count
            bh_type = all_bh_types.Item(count)
            BHObjects = Split(bh_type.BHElements, ",")
            For object_count = 0 To UBound(BHObjects)
                If BHObjects(object_count) <> "" Then
                    ranking = BHObjects(object_count) & "_" & bh_type.BHType
                    OutputXMLWriter.WriteStartElement("busyhour")
                    OutputXMLWriter.WriteAttributeString("name", ranking)
                    OutputXMLWriter.WriteAttributeString("description", BHObjects(object_count) & " " & bh_type.Description)
                    OutputXMLWriter.WriteAttributeString("criteria", bh_type.BHCriteria)
                    OutputXMLWriter.WriteAttributeString("sóurce", bh_type.BHSource)
                    OutputXMLWriter.WriteAttributeString("where", bh_type.BHWhere)
                    OutputXMLWriter.WriteEndElement()
                End If
            Next object_count
        Next count
        OutputXMLWriter.WriteEndElement()

    End Function


    Function MakeMetadata(ByRef Filename As String, ByRef BaseFilename As String, ByRef IntfFilename As String, ByRef OutputDir_Original As String, ByRef CustomInterface As Boolean) As logMessages


        Dim metafile As String
        Dim iFileNum As Short
        Dim logs As logMessages
        Dim message As logMessages.Message
        Dim count As Integer
        Dim OutputDir As String

        'update build number
        'Dim tp_excel = New TPExcelWriter
        'Dim updateBuild = tp_excel.updateBuildNumber(Filename, "metadata", OutputDir_Original)
        'tp_excel = Nothing

        tpAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & Filename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;ReadOnly=0;IMEX=1"""
        baseAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & BaseFilename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;ReadOnly=0;IMEX=1"""
        intfAdoConn = "Provider=Microsoft.Jet.OLEDB.4.0;Data Source=" & IntfFilename & ";Extended Properties=""Excel 8.0;MaxBufferSize=2048;HDR=YES;ReadOnly=0;IMEX=1"""

        tpConn = New System.Data.OleDb.OleDbConnection(tpAdoConn)
        baseConn = New System.Data.OleDb.OleDbConnection(baseAdoConn)
        intfConn = New System.Data.OleDb.OleDbConnection(intfAdoConn)

        tpConn.Open()
        baseConn.Open()
        intfConn.Open()

        logs = New logMessages


        Dim ClsInit As Boolean
        ExtStatementExecOrder = 0

        ClsInit = Initialize_Classes(logs)

        If ClsInit = False Then
            tpConn.Close()
            baseConn.Close()
            intfConn.Close()
            MsgBox("Run complete.")
        Else
            Initialize_InterfaceClasses(logs)
            OutputDir = OutputDir_Original & "\sql"
            Try
                If Not System.IO.Directory.Exists(OutputDir) Then
                    System.IO.Directory.CreateDirectory(OutputDir)
                End If
            Catch ex As Exception
                MsgBox("Create Directory '" & OutputDir & "' failed: " & ex.ToString)
            End Try

            If CustomInterface = False Then
                metafile = OutputDir & "\Tech_Pack_" & UseTechPackName & ".sql"
            Else
                metafile = OutputDir & "\Tech_Pack_" & UseIntfTechPackName & ".sql"
            End If
            iFileNum = FreeFile()
            FileOpen(iFileNum, metafile, OpenMode.Output)

            If CustomInterface = False Then
                PrintLine(iFileNum, Version_Information)
            Else
                PrintLine(iFileNum, Interface_Version_Information)
            End If

            If CustomInterface = False Then
                Metadata_Measurements(iFileNum)
                Metadata_TopologyData(iFileNum)
                Webportal_Configuration(iFileNum)
            End If


            Interface_DataFormat(iFileNum)
            Interface_DefaultTags(iFileNum)
            Interface_DataItem(iFileNum)
            If CustomInterface = False Then
                Metadata_Aggregation(iFileNum)
                Metadata_AggregationRules(iFileNum)
                TechPack_BusyHours(iFileNum)
                Metadata_BusyHourAggregationRules(iFileNum)
                TechPack_ExtraStatements(iFileNum, logs)
            End If
            Interface_Transformation(iFileNum)

            'Interface_DataInterfaces(iFileNum)
            'Interface_InterfaceMeasurements(iFileNum)

            FileClose(iFileNum)

            tpConn.Close()
            baseConn.Close()
            intfConn.Close()

            MsgBox("Run complete.")

        End If
        Return logs


    End Function
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
        Dim BusyHourObjects() As String
        Dim bh_count As Integer

        RuleID = 1

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)

            If mt.RankTable = False Then
                'Day-Raw aggregations
                If mt.CreateCountTable = False AndAlso mt.DayAggregation = True Then
                    PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAY", "RAW", mt.MeasurementTypeID, mt.MeasurementTypeID, "TOTAL", "DAY", ""))
                    RuleID += 1
                End If

                'Day-Count aggregations
                If mt.CreateCountTable = True AndAlso mt.DayAggregation = True Then
                    PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAY", "COUNT", mt.MeasurementTypeID, mt.MeasurementTypeID, "TOTAL", "DAY", ""))
                    RuleID += 1
                End If

                'DayBH-Raw aggregations (BHSRC)

                If mt.CreateCountTable = False AndAlso mt.ObjectBusyHours <> "" Then
                    BusyHourObjects = Split(mt.ObjectBusyHours, ",")
                    For bh_count = 0 To UBound(BusyHourObjects)
                        PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "RAW", mt.MeasurementTypeID, mt.MeasurementTypeID, "BHSRC", "DAY", BusyHourObjects(bh_count)))
                        RuleID += 1
                    Next bh_count
                End If

                'DayBH-Count aggregations (BHSRC)
                If mt.CreateCountTable = True AndAlso mt.ObjectBusyHours <> "" Then
                    BusyHourObjects = Split(mt.ObjectBusyHours, ",")
                    For bh_count = 0 To UBound(BusyHourObjects)
                        PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "COUNT", mt.MeasurementTypeID, mt.MeasurementTypeID, "BHSRC", "DAY", BusyHourObjects(bh_count)))
                        RuleID += 1
                    Next bh_count
                End If

                'WeekBH-DayBH aggregations (DAYBHCLASS)
                If mt.ObjectBusyHours <> "" Then
                    BusyHourObjects = Split(mt.ObjectBusyHours, ",")
                    For bh_count = 0 To UBound(BusyHourObjects)
                        PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "DAYBH", mt.MeasurementTypeID, mt.MeasurementTypeID, "DAYBHCLASS", "WEEK", BusyHourObjects(bh_count)))
                        RuleID += 1
                    Next bh_count
                End If

                'WeekBH-Count aggregations (DAYBHCLASS)
                'If mt.CountTable = True AndAlso mt.ObjectBusyHours <> "" Then
                'BusyHourObjects = Split(mt.ObjectBusyHours, ",")
                'For bh_count = 0 To UBound(BusyHourObjects)
                'PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "DAYBH", mt.MeasurementTypeID, mt.MeasurementTypeID, "DAYBHCLASS", "WEEK", BusyHourObjects(bh_count)))
                'RuleID += 1
                'Next bh_count
                'End If

                'MonthBH-DayBH aggregations (DAYBHCLASS)
                If mt.ObjectBusyHours <> "" Then
                    BusyHourObjects = Split(mt.ObjectBusyHours, ",")
                    For bh_count = 0 To UBound(BusyHourObjects)
                        PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "DAYBH", mt.MeasurementTypeID, mt.MeasurementTypeID, "DAYBHCLASS", "MONTH", BusyHourObjects(bh_count)))
                        RuleID += 1
                    Next bh_count
                End If

                'MonthBH-Count aggregations (DAYBHCLASS)
                'If mt.CountTable = True AndAlso mt.ObjectBusyHours <> "" Then
                'BusyHourObjects = Split(mt.ObjectBusyHours, ",")
                'For bh_count = 0 To UBound(BusyHourObjects)
                'PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "DAYBH", mt.MeasurementTypeID, mt.MeasurementTypeID, "DAYBHCLASS", "MONTH", BusyHourObjects(bh_count)))
                'RuleID += 1
                'Next bh_count
                'End If
            End If

            'DayBH-RankBH aggregation (RANKSRC)
            If mt.ObjectBusyHours <> "" AndAlso mt.RankTable = True Then
                mts2 = mts
                For mt_count2 = 1 To mts2.Count
                    mt2 = mts2.Item(mt_count2)
                    BusyHourObjects = Split(mt2.ObjectBusyHours, ",")
                    For bh_count = 0 To UBound(BusyHourObjects)
                        If BusyHourObjects(bh_count) = mt.ObjectBusyHours AndAlso mt2.RankTable = False Then
                            PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "RANKBH", mt2.MeasurementTypeID, mt.MeasurementTypeID, "RANKSRC", "DAY", BusyHourObjects(bh_count)))
                            RuleID += 1
                        End If
                    Next bh_count
                Next mt_count2
            End If

            'Week/MonthBH-RankBH aggregation (DAYBHCLASS)
            If mt.RankTable = True AndAlso mt.ObjectBusyHours <> "" Then
                'build rank list
                RankList = Split(mt.ObjectBusyHours, ",")
                mts2 = mts
                For mt_count2 = 1 To mts2.Count
                    mt2 = mts2.Item(mt_count2)
                    BusyHourObjects = Split(mt2.ObjectBusyHours, ",")
                    For bh_count = 0 To UBound(BusyHourObjects)
                        If BusyHourObjects(bh_count) = mt.ObjectBusyHours AndAlso mt2.RankTable = False Then
                            PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "RANKBH", mt2.MeasurementTypeID, mt.MeasurementTypeID, "DAYBHCLASS", "WEEK", BusyHourObjects(bh_count)))
                            RuleID += 1
                            PrintLine(iFileNum, PrintAggregationRule(RuleID, "DAYBH", "RANKBH", mt2.MeasurementTypeID, mt.MeasurementTypeID, "DAYBHCLASS", "MONTH", BusyHourObjects(bh_count)))
                            RuleID += 1
                        End If
                    Next bh_count
                Next mt_count2
            End If

            'Count-Raw aggregations
            If mt.CreateCountTable = True Then
                PrintLine(iFileNum, PrintAggregationRule(RuleID, "COUNT", "RAW", mt.MeasurementTypeID, mt.MeasurementTypeID, "COUNT", "DAY", ""))
                RuleID += 1
            End If
        Next mt_count

        PrintLine(iFileNum, "")

    End Sub

    'Function creates clauses for inserting AggregationRule information to DWH repository
    'Input parameters: iFileNum - handler for metadata sql file
    '
    'Uses Coversheet, MeasurementTypes from TP specification
    Sub Metadata_BusyHourAggregationRules(ByVal iFileNum As Short)

        Dim RuleID As Integer

        Dim RankList() As String
        Dim mts2 As MeasurementTypes
        Dim mt2 As MeasurementTypes.MeasurementType
        Dim mt_count2 As Integer
        Dim Inx As Integer
        Dim Added As Boolean

        Dim bh_sources() As String
        Dim bh_objects() As String
        Dim bh_elements() As String
        Dim CountMeasurement As Boolean

        Dim count As Integer
        Dim source_count As Integer
        Dim object_count As Integer
        Dim element_count As Integer
        Dim ranking As String
        Dim rankingTable As String

        Dim sourceTable As String
        Dim elementTimesAdded As Boolean
        Dim objectTimesAdded As Boolean

        'AGGREGATIONRULES
        RuleID = 1

        For count = 1 To all_bh_types.Count
            bh_type = all_bh_types.Item(count)
            bh_sources = Split(bh_type.BHSource, ",")
            bh_objects = Split(bh_type.BHObjects, ",")
            bh_elements = Split(bh_type.BHElements, ",")
            elementTimesAdded = False
            objectTimesAdded = False
            For source_count = 0 To UBound(bh_sources)
                If bh_sources(source_count) <> "" Then
                    'Loop through objects of busy hour 
                    For object_count = 0 To UBound(bh_objects)
                        objectTimesAdded = False
                        If bh_objects(object_count) <> "" Then
                            rankingTable = TechPackName & "_" & UCase(bh_objects(object_count)) & "BH"
                            ranking = bh_objects(object_count) & "_" & bh_type.BHType
                            'RankBH-Raw aggregation
                            If InStrRev(bh_sources(source_count), "_RAW") > 0 Then
                                If InStr(bh_sources(source_count), " ") = 0 Then
                                    sourceTable = bh_sources(source_count)
                                Else
                                    sourceTable = Left(bh_sources(source_count), InStr(bh_sources(source_count), " ") - 1)
                                End If
                                sourceTable = Replace(sourceTable, "_RAW", "")
                                PrintLine(iFileNum, PrintRankAggregationRule(RuleID, ranking, "RANKBH", "RAW", rankingTable, sourceTable, "RANKBH", "DAY"))
                                RuleID += 1
                            End If
                            'RankBH-Count aggregation
                            If InStrRev(bh_sources(source_count), "_COUNT") > 0 Then
                                If InStr(bh_sources(source_count), " ") = 0 Then
                                    sourceTable = bh_sources(source_count)
                                Else
                                    sourceTable = Left(bh_sources(source_count), InStr(bh_sources(source_count), " ") - 1)
                                End If
                                sourceTable = Replace(sourceTable, "_COUNT", "")
                                PrintLine(iFileNum, PrintRankAggregationRule(RuleID, ranking, "RANKBH", "COUNT", rankingTable, sourceTable, "RANKBH", "DAY"))
                                RuleID += 1
                            End If
                            If objectTimesAdded = False Then
                                'Week/MonthRankBH-RankBH aggregation (RANKBHCLASS)
                                PrintLine(iFileNum, PrintRankAggregationRule(RuleID, ranking, "RANKBH", "RANKBH", rankingTable, rankingTable, "RANKBHCLASS", "WEEK"))
                                RuleID += 1
                                PrintLine(iFileNum, PrintRankAggregationRule(RuleID, ranking, "RANKBH", "RANKBH", rankingTable, rankingTable, "RANKBHCLASS", "MONTH"))
                                RuleID += 1
                                objectTimesAdded = True
                            End If
                        End If
                    Next object_count

                    'Loop through elements of busy hour 
                    For element_count = 0 To UBound(bh_elements)
                        elementTimesAdded = False
                        If bh_elements(element_count) <> "" Then
                            rankingTable = TechPackName & "_" & "ELEMBH"
                            ranking = bh_elements(element_count) & "_" & bh_type.BHType
                            'RankBH-Raw aggregation
                            If InStrRev(bh_sources(source_count), "_RAW") > 0 Then
                                If InStr(bh_sources(source_count), " ") = 0 Then
                                    sourceTable = bh_sources(source_count)
                                Else
                                    sourceTable = Left(bh_sources(source_count), InStr(bh_sources(source_count), " ") - 1)
                                End If
                                sourceTable = Replace(sourceTable, "_RAW", "")
                                PrintLine(iFileNum, PrintRankAggregationRule(RuleID, ranking, "RANKBH", "RAW", rankingTable, sourceTable, "RANKBH", "DAY"))
                                RuleID += 1
                            End If
                            'RankBH-Count aggregation
                            If InStrRev(bh_sources(source_count), "_COUNT") > 0 Then
                                If InStr(bh_sources(source_count), " ") = 0 Then
                                    sourceTable = bh_sources(source_count)
                                Else
                                    sourceTable = Left(bh_sources(source_count), InStr(bh_sources(source_count), " ") - 1)
                                End If
                                sourceTable = Replace(sourceTable, "_COUNT", "")
                                PrintLine(iFileNum, PrintRankAggregationRule(RuleID, ranking, "RANKBH", "COUNT", rankingTable, sourceTable, "RANKBH", "DAY"))
                                RuleID += 1
                            End If
                            If elementTimesAdded = False Then
                                'Week/MonthRankBH-RankBH aggregation (RANKBHCLASS)
                                PrintLine(iFileNum, PrintRankAggregationRule(RuleID, ranking, "RANKBH", "RANKBH", rankingTable, rankingTable, "RANKBHCLASS", "WEEK"))
                                RuleID += 1
                                PrintLine(iFileNum, PrintRankAggregationRule(RuleID, ranking, "RANKBH", "RANKBH", rankingTable, rankingTable, "RANKBHCLASS", "MONTH"))
                                RuleID += 1
                                elementTimesAdded = True
                            End If
                        End If
                    Next element_count
                End If
            Next source_count
        Next count

        PrintLine(iFileNum, "")

    End Sub
    'Function creates clauses for inserting Aggregation information to DWH repository
    'Input parameters: iFileNum - handler for metadata sql file
    '
    'Uses Coversheet, MeasurementTypes from TP specification
    Sub Metadata_Aggregation(ByVal iFileNum As Short)

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.RankTable = False Then
                If mt.DayAggregation = True Then
                    If mt.CreateCountTable = True Then
                        PrintLine(iFileNum, "insert into Aggregation " & _
                        "(aggregation, versionid, aggregationtype, aggregationscope) " & _
                        "values (" & _
                        "'" & mt.MeasurementTypeID & "_COUNT" & "'," & _
                        "'" & PackRel & "'," & _
                        "'TOTAL'," & _
                        "'COUNT');")
                    End If
                    PrintLine(iFileNum, "insert into Aggregation " & _
                    "(aggregation, versionid, aggregationtype, aggregationscope) " & _
                    "values (" & _
                    "'" & mt.MeasurementTypeID & "_DAY" & "'," & _
                    "'" & PackRel & "'," & _
                    "'TOTAL'," & _
                    "'DAY');")
                End If
                Dim BusyHourObjects() As String
                Dim bh_count As Integer
                If mt.ObjectBusyHours <> "" Then
                    BusyHourObjects = Split(mt.ObjectBusyHours, ",")
                    For bh_count = 0 To UBound(BusyHourObjects)
                        PrintLine(iFileNum, "insert into Aggregation " & _
                        "(aggregation, versionid, aggregationtype, aggregationscope) " & _
                        "values (" & _
                        "'" & mt.MeasurementTypeID & "_DAYBH" & "_" & BusyHourObjects(bh_count) & "'," & _
                        "'" & PackRel & "'," & _
                        "'DAYBH'," & _
                        "'DAY');")

                        PrintLine(iFileNum, "insert into Aggregation " & _
                        "(aggregation, versionid, aggregationtype, aggregationscope) " & _
                        "values (" & _
                        "'" & mt.MeasurementTypeID & "_WEEKBH" & "_" & BusyHourObjects(bh_count) & "'," & _
                        "'" & PackRel & "'," & _
                        "'DAYBH'," & _
                        "'WEEK');")

                        PrintLine(iFileNum, "insert into Aggregation " & _
                        "(aggregation, versionid, aggregationtype, aggregationscope) " & _
                        "values (" & _
                        "'" & mt.MeasurementTypeID & "_MONTHBH" & "_" & BusyHourObjects(bh_count) & "'," & _
                        "'" & PackRel & "'," & _
                        "'DAYBH'," & _
                        "'MONTH');")
                    Next bh_count
                End If
            End If
        Next mt_count

        PrintLine(iFileNum, "")

    End Sub
    Function PrintAggregationRule(ByVal RuleID As Integer, ByVal TargetLevel As String, ByVal SourceLevel As String, ByVal TargetMeas As String, ByVal SourceMeas As String, ByVal AggrType As String, ByVal AggrLevel As String, ByVal bhObject As String) As String

        Dim InsertColumns As String
        If SourceLevel = "DAYBH" And AggrType = "DAYBHCLASS" Then
            AggrType = AggrType & "_" & SourceLevel
        End If

        InsertColumns = "insert into AggregationRule " & _
        "(aggregation, versionid, ruleid, target_mtableid, target_type, target_level, target_table, source_mtableid, source_type, source_level, source_table, ruletype, aggregationscope) " & _
        "values ("
        If TargetLevel = "DAYBH" AndAlso AggrLevel = "WEEK" Then
            '"'" & TargetMeas & "_" & "WEEKBH" & "'," & _
            '"'" & TargetMeas & "_" & "WEEKBH" & "_" & TPVersion & "'," & _
            '"'" & TargetMeas & "_" & "WEEKBH" & "_" & TPVersion & "_" & bhObject & "'," & _
            PrintAggregationRule = InsertColumns & _
            "'" & TargetMeas & "_" & "WEEKBH" & "_" & bhObject & "'," & _
            "'" & PackRel & "'," & _
            RuleID & "," & _
            "'" & TechPackName & ":" & TPVersion & ":" & TargetMeas + ":" & TargetLevel & "'," & _
            "'" & TargetMeas & "'," & _
            "'" & TargetLevel & "'," & _
            "'" & TargetMeas & "_" & TargetLevel & "'," & _
            "'" & TechPackName & ":" & TPVersion & ":" & SourceMeas + ":" & SourceLevel & "'," & _
            "'" & SourceMeas & "'," & _
            "'" & SourceLevel & "'," & _
            "'" & SourceMeas & "_" & SourceLevel & "'," & _
            "'" & AggrType & "'," & _
            "'" & AggrLevel & "');"
        ElseIf TargetLevel = "DAYBH" AndAlso AggrLevel = "MONTH" Then
            '"'" & TargetMeas & "_" & "MONTHBH" & "'," & _
            '"'" & TargetMeas & "_" & "MONTHBH" & "_" & TPVersion & "'," & _
            '"'" & TargetMeas & "_" & "MONTHBH" & "_" & TPVersion & "_" & bhObject & "'," & _
            PrintAggregationRule = InsertColumns & _
            "'" & TargetMeas & "_" & "MONTHBH" & "_" & bhObject & "'," & _
            "'" & PackRel & "'," & _
            RuleID & "," & _
            "'" & TechPackName & ":" & TPVersion & ":" & TargetMeas + ":" & TargetLevel & "'," & _
            "'" & TargetMeas & "'," & _
            "'" & TargetLevel & "'," & _
            "'" & TargetMeas & "_" & TargetLevel & "'," & _
            "'" & TechPackName & ":" & TPVersion & ":" & SourceMeas + ":" & SourceLevel & "'," & _
            "'" & SourceMeas & "'," & _
            "'" & SourceLevel & "'," & _
            "'" & SourceMeas & "_" & SourceLevel & "'," & _
            "'" & AggrType & "'," & _
            "'" & AggrLevel & "');"
        Else
            If bhObject = "" Then
                '"'" & TargetMeas & "_" & TargetLevel & "'," & _
                '"'" & TargetMeas & "_" & TargetLevel & "_" & TPVersion & "'," & _
                PrintAggregationRule = InsertColumns & _
                "'" & TargetMeas & "_" & TargetLevel & "'," & _
                "'" & PackRel & "'," & _
                RuleID & "," & _
                "'" & TechPackName & ":" & TPVersion & ":" & TargetMeas + ":" & TargetLevel & "'," & _
                "'" & TargetMeas & "'," & _
                "'" & TargetLevel & "'," & _
                "'" & TargetMeas & "_" & TargetLevel & "'," & _
                "'" & TechPackName & ":" & TPVersion & ":" & SourceMeas + ":" & SourceLevel & "'," & _
                "'" & SourceMeas & "'," & _
                "'" & SourceLevel & "'," & _
                "'" & SourceMeas & "_" & SourceLevel & "'," & _
                "'" & AggrType & "'," & _
                "'" & AggrLevel & "');"
            Else
                '"'" & TargetMeas & "_" & TargetLevel & "'," & _
                '"'" & TargetMeas & "_" & TargetLevel & "_" & TPVersion & "'," & _
                '"'" & TargetMeas & "_" & TargetLevel & "_" & TPVersion & "_" & bhObject & "'," & _
                PrintAggregationRule = InsertColumns & _
                "'" & TargetMeas & "_" & TargetLevel & "_" & bhObject & "'," & _
                "'" & PackRel & "'," & _
                RuleID & "," & _
                "'" & TechPackName & ":" & TPVersion & ":" & TargetMeas + ":" & TargetLevel & "'," & _
                "'" & TargetMeas & "'," & _
                "'" & TargetLevel & "'," & _
                "'" & TargetMeas & "_" & TargetLevel & "'," & _
                "'" & TechPackName & ":" & TPVersion & ":" & SourceMeas + ":" & SourceLevel & "'," & _
                "'" & SourceMeas & "'," & _
                "'" & SourceLevel & "'," & _
                "'" & SourceMeas & "_" & SourceLevel & "'," & _
                "'" & AggrType & "'," & _
                "'" & AggrLevel & "');"
            End If
        End If

    End Function
    Function PrintRankAggregationRule(ByVal RuleID As Integer, ByVal RankInx As String, ByVal TargetLevel As String, ByVal SourceLevel As String, ByVal TargetMeas As String, ByVal SourceMeas As String, ByVal AggrType As String, ByVal AggrLevel As String) As String

        Dim InsertColumns As String       
        InsertColumns = "insert into AggregationRule (aggregation, versionid, ruleid, target_mtableid, target_type,target_level, target_table, source_mtableid, source_type, source_level, source_table, ruletype, aggregationscope,bhtype) values ('"

        If AggrLevel = "WEEK" Then
            'TargetMeas & "_" & AggrLevel & TargetLevel & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
            'TargetMeas & "_" & AggrLevel & TargetLevel & "_" & TPVersion & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
            PrintRankAggregationRule = InsertColumns & _
            TargetMeas & "_" & AggrLevel & TargetLevel & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
            TechPackName & ":" & TPVersion & ":" & TargetMeas + ":" & TargetLevel & "','" & _
            TargetMeas & "','" & TargetLevel & "','" & TargetMeas & "_" & TargetLevel & "','" & _
            TechPackName & ":" & TPVersion & ":" & SourceMeas + ":" & SourceLevel & "','" & _
            SourceMeas & "','" & SourceLevel & "','" & SourceMeas & "_" & SourceLevel & "'," & _
            "'" & AggrType & "','" & AggrLevel & "','" & RankInx & "');"
        ElseIf AggrLevel = "MONTH" Then
            'TargetMeas & "_" & AggrLevel & TargetLevel & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
            'TargetMeas & "_" & AggrLevel & TargetLevel & "_" & TPVersion & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
            PrintRankAggregationRule = InsertColumns & _
            TargetMeas & "_" & AggrLevel & TargetLevel & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
            TechPackName & ":" & TPVersion & ":" & TargetMeas + ":" & TargetLevel & "','" & _
            TargetMeas & "','" & TargetLevel & "','" & TargetMeas & "_" & TargetLevel & "','" & _
            TechPackName & ":" & TPVersion & ":" & SourceMeas + ":" & SourceLevel & "','" & _
            SourceMeas & "','" & SourceLevel & "','" & SourceMeas & "_" & SourceLevel & "'," & _
            "'" & AggrType & "','" & AggrLevel & "','" & RankInx & "');"
        Else
            'TargetMeas & "_" & TargetLevel & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
            'TargetMeas & "_" & TargetLevel & "_" & TPVersion & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
            PrintRankAggregationRule = InsertColumns & _
            TargetMeas & "_" & TargetLevel & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
            TechPackName & ":" & TPVersion & ":" & TargetMeas + ":" & TargetLevel & "','" & _
            TargetMeas & "','" & TargetLevel & "','" & TargetMeas & "_" & TargetLevel & "','" & _
            TechPackName & ":" & TPVersion & ":" & SourceMeas + ":" & SourceLevel & "','" & _
            SourceMeas & "','" & SourceLevel & "','" & TargetMeas & "_RANKBH_" & RankInx & "'," & _
            "'" & AggrType & "','" & AggrLevel & "','" & RankInx & "');"
        End If


    End Function
    Function PrintViewAggregationRule(ByVal RuleID As Integer, ByVal RankInx As String, ByVal TargetLevel As String, ByVal SourceLevel As String, ByVal TargetMeas As String, ByVal SourceMeas As String, ByVal AggrType As String, ByVal AggrLevel As String) As String

        Dim InsertColumns As String
        InsertColumns = "insert into AggregationRule (aggregation, versionid, ruleid, target_mtableid, target_type,target_level, target_table, source_mtableid, source_type, source_level, source_table, ruletype, aggregationscope) values ('"
        'TargetMeas & "_" & TargetLevel & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
        'TargetMeas & "_" & TargetLevel & "_" & TPVersion & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
        PrintViewAggregationRule = InsertColumns & _
        TargetMeas & "_" & TargetLevel & "_" & RankInx & "','" & PackRel & "'," & RuleID & ",'" & _
        TechPackName & ":" & TPVersion & ":" & TargetMeas + ":" & TargetLevel & "','" & _
        TargetMeas & "','" & TargetLevel & "','" & TargetMeas & "_" & TargetLevel & "','" & _
        TechPackName & ":" & TPVersion & ":" & SourceMeas + ":" & SourceLevel & "','" & _
        SourceMeas & "','" & SourceLevel & "','" & TargetMeas & "_CALC_" & RankInx & "'," & _
        "'" & AggrType & "','" & AggrLevel & "');"


    End Function

    Sub TechPack_BusyHours(ByVal iFileNum As Short)

        Dim BHInserts As String
        Dim comb_BHs As BusyHours
        Dim comb_bh_count As Integer
        Dim count As Integer
        Dim object_count As Integer
        Dim ranking As String
        Dim BHTables() As String
        Dim obj_count As Integer
        Dim BHViews As String
        Dim Inx As Integer
        Dim BHObjects() As String
        Dim BHKeyValues() As String

        Dim rankingTable As String

        'objects
        For count = 1 To all_bh_types.Count
            'MULTIPLE object bhs
            bh_type = all_bh_types.Item(count)
            BHObjects = Split(bh_type.BHObjects, ",")
            For object_count = 0 To UBound(BHObjects)
                If BHObjects(object_count) <> "" Then
                    rankingTable = TechPackName & "_" & UCase(BHObjects(object_count)) & "BH"
                    ranking = BHObjects(object_count) & "_" & bh_type.BHType

                    'Busy Hour Aggregation clause
                    PrintLine(iFileNum, "   insert into Aggregation " & _
                    "(aggregation, versionid, aggregationtype, aggregationscope) " & _
                    "values (" & _
                    "'" & rankingTable & "_RANKBH" & "_" & ranking & "'," & _
                    "'" & PackRel & "'," & _
                    "'RANKBH'," & _
                    "'DAY');")

                    PrintLine(iFileNum, "insert into Aggregation " & _
                    "(aggregation, versionid, aggregationtype, aggregationscope) " & _
                    "values (" & _
                    "'" & rankingTable & "_WEEKRANKBH" & "_" & ranking & "'," & _
                    "'" & PackRel & "'," & _
                    "'RANKBH'," & _
                    "'WEEK');")

                    PrintLine(iFileNum, "insert into Aggregation " & _
                    "(aggregation, versionid, aggregationtype, aggregationscope) " & _
                    "values (" & _
                    "'" & rankingTable & "_MONTHRANKBH" & "_" & ranking & "'," & _
                    "'" & PackRel & "'," & _
                    "'RANKBH'," & _
                    "'MONTH');")
                    'busy hour insert clause
                    BHInserts = "DELETE FROM " & Replace(rankingTable, "DC_", "DIM_") & "_BHTYPE " & _
                    "WHERE BHTYPE = " & _
                    "'" & ranking & "';"

                    BHInserts = Replace(BHInserts, "'", "''")
                    ExtStatementExecOrder += 1
                    PrintLine(iFileNum, "insert into ExternalStatement " & _
                    "(versionid,statementname, executionorder, dbconnection, statement) " & _
                    "values (" & _
                    "'" & PackRel & "'," & _
                    "'DELETE FROM " & Replace(rankingTable, "DC_", "DIM_") & "_BHTYPE" & "_" & ranking & "'," & _
                    ExtStatementExecOrder & "," & _
                    "'dwh'," & _
                    "'" & BHInserts & "');")

                    'busy hour insert clause
                    BHInserts = "INSERT INTO " & Replace(rankingTable, "DC_", "DIM_") & "_BHTYPE " & _
                    "( BHTYPE,DESCRIPTION ) " & _
                    "values (" & _
                    "'" & ranking & "'," & _
                    "'" & BHObjects(object_count) & " " & bh_type.Description & "');"

                    BHInserts = Replace(BHInserts, "'", "''")
                    ExtStatementExecOrder += 1
                    PrintLine(iFileNum, "insert into ExternalStatement " & _
                    "(versionid,statementname, executionorder, dbconnection, statement) " & _
                    "values (" & _
                    "'" & PackRel & "'," & _
                    "'INSERT INTO " & Replace(rankingTable, "DC_", "DIM_") & "_BHTYPE" & "_" & ranking & "'," & _
                    ExtStatementExecOrder & "," & _
                    "'dwh'," & _
                    "'" & BHInserts & "');")

                    'busy hour view clause
                    For obj_count = 1 To all_bhobjs.Count
                        bhobj = all_bhobjs.Item(obj_count)
                        If bhobj.BHObject = BHObjects(object_count) AndAlso LCase(bhobj.Type) = "object" Then
                            Exit For
                        End If
                    Next obj_count

                    BHViews = "IF (SELECT count(*) FROM sys.sysviews where viewname = '" & rankingTable & "_RANKBH_" & ranking & "') > 0 "
                    BHViews &= " DROP VIEW " & rankingTable & "_RANKBH_" & ranking & " "
                    'BHViews &= " ELSE "
                    BHViews &= " BEGIN "
                    BHViews &= "CREATE VIEW " & rankingTable & "_RANKBH_" & ranking & " ( "
                    BHViews &= bhobj.Keys & ",DATE_ID,HOUR_ID,ROWSTATUS,BHTYPE,BHVALUE,PERIOD_DURATION,DC_RELEASE,DC_SOURCE,DC_TIMEZONE ) "
                    BHViews &= "as SELECT "
                    BHViews &= bhobj.KeyValues & ", DATE_ID, HOUR_ID, ROWSTATUS, '" & ranking & "', "
                    BHViews &= "cast((" & bh_type.BHCriteria & ") as numeric(18,8)), "



                    'FIX src1 to PERIOD_DURATION if there are multiple BH sources
                    If UBound(Split(bh_type.BHSource, ",")) > 0 Then
                        BHViews &= "sum(src1.PERIOD_DURATION),src1.DC_RELEASE,src1.DC_SOURCE,src1.DC_TIMEZONE FROM "
                    Else
                        BHViews &= "sum(PERIOD_DURATION),DC_RELEASE,DC_SOURCE,DC_TIMEZONE FROM "
                    End If


                    'BHTables = 
                    'For Inx = 0 To 
                    'If Inx > 0 Then
                    'BHViews &= ","
                    'End 'If

                    'BHViews &= BHTables(Inx)
                    'If UBound(BHTables) > 0 Then
                    'BHViews &= " src" & (Inx + 1).ToString & " "
                    'End If

                    'Next Inx
                    BHViews &= " " & bh_type.BHSource & " "

                    If bh_type.BHWhere <> "" Then
                        BHViews &= " " & bh_type.BHWhere & " "
                    End If
                    BHViews &= " GROUP BY "

                    'If UBound(BHTables) > 0 Then

                    'BHKeyValues = Split(bhobj.KeyValues, ",")

                    'For Inx = 0 To UBound(BHKeyValues)
                    'If BHKeyValues(Inx) <> "" Then
                    'If Inx > 0 Then
                    'BHViews &= ", "
                    'End If
                    'BHViews &= "src1." & BHKeyValues(Inx)
                    'End If

                    'Next Inx

                    'BHViews &= ", DATE_ID, HOUR_ID, '" & ranking & "' "
                    'Else
                    BHViews &= bhobj.KeyValues & ", DATE_ID, HOUR_ID, ROWSTATUS, '" & ranking & "',DC_RELEASE,DC_SOURCE,DC_TIMEZONE "
                    'End If
                    BHViews &= " END"

                    If BHViews <> "" Then
                        BHViews = Replace(BHViews, "'", "''")
                        ExtStatementExecOrder += 1
                        PrintLine(iFileNum, "insert into ExternalStatement " & _
                        "(versionid,statementname, executionorder, dbconnection, statement) " & _
                        "values (" & _
                        "'" & PackRel & "'," & _
                        "'CREATE VIEW " & rankingTable & "_RANKBH_" & ranking & "'," & _
                        ExtStatementExecOrder & "," & _
                        "'dwh'," & _
                        "'" & BHViews & "');")
                    End If
                End If
            Next object_count
        Next count

        'elements
        rankingTable = TechPackName & "_ELEMBH"
        For count = 1 To all_bh_types.Count
            bh_type = all_bh_types.Item(count)
            BHObjects = Split(bh_type.BHElements, ",")
            For object_count = 0 To UBound(BHObjects)
                If BHObjects(object_count) <> "" Then

                    ranking = BHObjects(object_count) & "_" & bh_type.BHType

                    'Busy Hour Aggregation clause
                    PrintLine(iFileNum, "insert into Aggregation " & _
                    "(aggregation, versionid, aggregationtype, aggregationscope) " & _
                    "values (" & _
                    "'" & rankingTable & "_RANKBH" & "_" & ranking & "'," & _
                    "'" & PackRel & "'," & _
                    "'RANKBH'," & _
                    "'DAY');")

                    PrintLine(iFileNum, "insert into Aggregation " & _
                    "(aggregation, versionid, aggregationtype, aggregationscope) " & _
                    "values (" & _
                    "'" & rankingTable & "_WEEKRANKBH" & "_" & ranking & "'," & _
                    "'" & PackRel & "'," & _
                    "'RANKBH'," & _
                    "'WEEK');")

                    PrintLine(iFileNum, "insert into Aggregation " & _
                    "(aggregation, versionid, aggregationtype, aggregationscope) " & _
                    "values (" & _
                    "'" & rankingTable & "_MONTHRANKBH" & "_" & ranking & "'," & _
                    "'" & PackRel & "'," & _
                    "'RANKBH'," & _
                    "'MONTH');")

                    'busy hour insert clause
                    BHInserts = "DELETE FROM " & Replace(rankingTable, "DC_", "DIM_") & "_BHTYPE " & _
                    "where BHTYPE = " & _
                    "'" & ranking & "';"

                    BHInserts = Replace(BHInserts, "'", "''")

                    ExtStatementExecOrder += 1
                    PrintLine(iFileNum, "insert into ExternalStatement " & _
                    "(versionid,statementname, executionorder, dbconnection, statement) " & _
                    "values (" & _
                    "'" & PackRel & "'," & _
                    "'DELETE FROM " & Replace(rankingTable, "DC_", "DIM_") & "_BHTYPE_" & ranking & "'," & _
                    ExtStatementExecOrder & "," & _
                    "'dwh'," & _
                    "'" & BHInserts & "');")

                    'busy hour insert clause
                    BHInserts = "INSERT INTO " & Replace(rankingTable, "DC_", "DIM_") & "_BHTYPE " & _
                    "( BHTYPE,DESCRIPTION ) " & _
                    "values (" & _
                    "'" & ranking & "'," & _
                    "'" & BHObjects(object_count) & " " & bh_type.Description & "');"

                    BHInserts = Replace(BHInserts, "'", "''")

                    ExtStatementExecOrder += 1
                    PrintLine(iFileNum, "insert into ExternalStatement " & _
                    "(versionid,statementname, executionorder, dbconnection, statement) " & _
                    "values (" & _
                    "'" & PackRel & "'," & _
                    "'INSERT INTO " & Replace(rankingTable, "DC_", "DIM_") & "_BHTYPE_" & ranking & "'," & _
                    ExtStatementExecOrder & "," & _
                    "'dwh'," & _
                    "'" & BHInserts & "');")


                    'busy hour view clause
                    For obj_count = 1 To all_bhobjs.Count
                        bhobj = all_bhobjs.Item(obj_count)
                        If bhobj.BHObject = BHObjects(object_count) AndAlso LCase(bhobj.Type) = "element" Then
                            Exit For
                        End If
                    Next obj_count

                    BHViews = "IF (SELECT count(*) FROM sys.sysviews where viewname = '" & rankingTable & "_RANKBH_" & ranking & "') > 0 "
                    BHViews &= " DROP VIEW " & rankingTable & "_RANKBH_" & ranking & " "
                    'BHViews &= " ELSE "
                    BHViews &= " BEGIN "
                    BHViews &= "CREATE VIEW " & rankingTable & "_RANKBH_" & ranking & " ( "
                    BHViews &= bhobj.Keys & ",DATE_ID,HOUR_ID,ROWSTATUS,BHTYPE,BHVALUE,PERIOD_DURATION,DC_RELEASE,DC_SOURCE,DC_TIMEZONE ) "
                    BHViews &= "as SELECT "
                    BHViews &= bhobj.KeyValues & ", DATE_ID, HOUR_ID, ROWSTATUS, '" & ranking & "', "
                    BHViews &= "cast((" & bh_type.BHCriteria & ") as numeric(18,8)), "

                    'FIX src1 to PERIOD_DURATION if there are multiple BH sources
                    If UBound(Split(bh_type.BHSource, ",")) > 0 Then
                        BHViews &= "sum(src1.PERIOD_DURATION),src1.DC_RELEASE,src1.DC_SOURCE,src1.DC_TIMEZONE FROM "
                    Else
                        BHViews &= "sum(PERIOD_DURATION),DC_RELEASE,DC_SOURCE,DC_TIMEZONE FROM "
                    End If

                    'BHTables = Split(bh_type.BHSource, ",")
                    'For Inx = 0 To UBound(BHTables)
                    'If Inx > 0 Then
                    'BHViews &= ","
                    'End If

                    'BHViews &= BHTables(Inx)
                    'If UBound(BHTables) > 0 Then
                    'BHViews &= "src" & (Inx + 1).ToString & " "
                    'End If

                    'Next Inx
                    BHViews &= " " & bh_type.BHSource & " "

                    If bh_type.BHWhere <> "" Then
                        BHViews &= bh_type.BHWhere & " "
                    End If
                    BHViews &= " GROUP BY "

                    'If UBound(BHTables) > 0 Then

                    'BHKeyValues = Split(bhobj.KeyValues, ",")

                    'For Inx = 0 To UBound(BHKeyValues)
                    'If BHKeyValues(Inx) <> "" Then
                    'If Inx > 0 Then
                    'BHViews &= ", "
                    'End If
                    'BHViews &= "src1." & BHKeyValues(Inx)
                    'End If

                    'Next Inx

                    'BHViews &= ", DATE_ID, HOUR_ID, '" & ranking & "';"
                    'Else
                    BHViews &= bhobj.KeyValues & ", DATE_ID, HOUR_ID, ROWSTATUS, '" & ranking & "',DC_RELEASE,DC_SOURCE,DC_TIMEZONE "
                    'End If
                    BHViews &= " END"

                    If BHViews <> "" Then
                        BHViews = Replace(BHViews, "'", "''")
                        ExtStatementExecOrder += 1
                        PrintLine(iFileNum, "insert into ExternalStatement " & _
                        "(versionid,statementname, executionorder, dbconnection, statement) " & _
                        "values (" & _
                        "'" & PackRel & "'," & _
                        "'CREATE VIEW " & rankingTable & "_RANKBH_" & ranking & "'," & _
                        ExtStatementExecOrder & "," & _
                        "'dwh'," & _
                        "'" & BHViews & "');")
                    End If
                End If
            Next object_count
        Next count

        PrintLine(iFileNum, "")

    End Sub
    Sub TechPack_ExtraStatements(ByVal iFileNum As Short, ByRef logs As logMessages)
        ExtStatementExecOrder = PrintExternalStatements(tpConn, iFileNum, "Views", ExtStatementExecOrder, "dwh")
        ExtStatementExecOrder = PrintExternalStatements(baseConn, iFileNum, "Views", ExtStatementExecOrder, "dwh")
        ExtStatementExecOrder = PrintExternalStatements(tpConn, iFileNum, "Procedures", ExtStatementExecOrder, "dwh")
        ExtStatementExecOrder = PrintExternalStatements(tpConn, iFileNum, "Inserts", ExtStatementExecOrder, "dwh")
        ExtStatementExecOrder = PrintVectorStatements(tpConn, iFileNum, "Vectors", ExtStatementExecOrder, "dwh", logs)
        ExtStatementExecOrder = PrintExternalStatements(tpConn, iFileNum, "Metadata Inserts", ExtStatementExecOrder, "dwhrep")
        PrintLine(iFileNum, "")
    End Sub
    Function PrintExternalStatements(ByRef conn As System.Data.OleDb.OleDbConnection, ByVal iFileNum As Short, ByRef Sheet As String, ByRef exec_order As Integer, ByRef database As String)
        On Error Resume Next
        Dim Name As String
        Dim Definition As String
        Dim InsertStatus As String

        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [" & Sheet & "$]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            If Trim(dbReader.GetValue(0).ToString()) = "" Then
                Exit While
            Else
                Name = Replace(dbReader.GetValue(0).ToString(), "(TPNAME)", Replace(TechPackName, "DC_", ""))
                Definition = Replace(dbReader.GetValue(1).ToString(), "(TPNAME)", Replace(TechPackName, "DC_", ""))

                If Definition <> "" Then
                    'InsertStatus = dbReader.GetValue(2).ToString()
                    Definition = Replace(Definition, "'", "''")
                    exec_order += 1
                    PrintLine(iFileNum, "insert into ExternalStatement " & _
                    "(versionid,statementname, executionorder, dbconnection, statement) " & _
                    "values (" & _
                    "'" & PackRel & "'," & _
                    "'" & Name & "'," & _
                    exec_order & "," & _
                    "'" & database & "'," & _
                    "'" & Definition & "');")
                End If
            End If
        End While
        dbReader.Close()
        dbCommand.Dispose()
        Return exec_order

    End Function
    Function PrintVectorStatements(ByRef conn As System.Data.OleDb.OleDbConnection, ByVal iFileNum As Short, ByRef Sheet As String, ByRef exec_order As Integer, ByRef database As String, ByRef logs As logMessages)
        On Error Resume Next
        Dim Name As String
        Dim ReleaseName As String
        Dim PrevRelease As String
        Dim NameFlag As Boolean
        Dim Value As String
        Dim Counter As String
        Dim PrevCounter As String
        Dim Definition As String
        Dim PrevName As String
        Dim Continue As Boolean
        Dim VendorRelease As String
        Dim SameDefinition As Boolean
        Dim TmpCount As Integer

        TmpCount = -1
        SameDefinition = True
        Continue = True
        dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [" & Sheet & "$]", conn)
        dbReader = dbCommand.ExecuteReader()
        While (dbReader.Read())
            NameFlag = False

            If dbReader.GetValue(0).ToString() = "" Then
                Exit While
            Else
                Counter = dbReader.GetValue(1).ToString()

                If PrevCounter <> Counter Then
                    TmpCount = TmpCount + 1
                End If

                Name = Replace(dbReader.GetValue(0).ToString(), "DC_", "DIM_") & "_" & Counter
                VendorRelease = Trim(dbReader.GetValue(6).ToString())

                If (VendorRelease <> "") Then
                    ReleaseName = Replace(dbReader.GetValue(0).ToString(), "DC_", "DIM_") & "_" & Counter & "_" & VendorRelease
                    NameFlag = True
                End If


                If (PrevRelease <> VendorRelease) And PrevName <> "" Then
                    SameDefinition = False
                End If

                If Continue = False And SameDefinition = False Or (PrevCounter <> Counter And PrevCounter <> "") Then
                    PrevName = PrevName & "_" & TmpCount
                    'insert previous
                    Definition = Replace(Definition, "'", "''")
                    exec_order += 1
                    PrintLine(iFileNum, "insert into ExternalStatement " & _
                    "(versionid,statementname, executionorder, dbconnection, statement) " & _
                    "values (" & _
                    "'" & PackRel & "'," & _
                    "'insert into " & PrevName & "_' || dateformat(today(), 'yyyy-mm-dd')," & _
                    exec_order & "," & _
                    "'" & database & "'," & _
                    "'" & Definition & "');")

                    SameDefinition = True
                    PrevRelease = VendorRelease
                    Definition = ""
                Else
                    Continue = False
                End If

                If (Continue = True) Then
                    Definition = ""
                End If

            End If

            Value = ""
            Dim first_value As String
            Dim value_type As String

            first_value = ""
            value_type = ""

            first_value = dbReader.GetValue(3).ToString()
            value_type = dbReader.GetString(5)
            If value_type <> "" Then
                If first_value = "+" Then
                    Value = first_value & " " & dbReader.GetValue(4).ToString() & " " & value_type
                Else
                    Value = first_value & " - " & dbReader.GetValue(4).ToString() & " " & value_type
                End If
            Else
                If first_value = "+" Then
                    Value = first_value & " " & dbReader.GetValue(4).ToString()
                Else
                    Value = first_value & " - " & dbReader.GetValue(4).ToString()
                End If
            End If

            If Definition = "" Then
                Definition &= "delete from " & Name & " where DC_RELEASE = '" & VendorRelease & "'; "
            End If

            If Value <> "" Then
                Definition &= "insert into " & Name & _
                " (" & Counter & "_DCVECTOR, " & Counter & "_VALUE, DC_RELEASE) " & _
                "values (" & _
                dbReader.GetValue(2).ToString() & "," & _
                "'" & Value & "'," & _
                "'" & Trim(dbReader.GetValue(6).ToString()) & "'); "
            Else
                logs.AddLogText("Description Values for counter '" + Counter + "' table '" & Name & "' is empty.")
            End If

            If Definition.Length > 32000 Then
                logs.AddLogText("External statement sql-clause exceeds maximum length in table '" & Name & "'.")
            End If

            If (NameFlag = False) Then
                PrevName = Name
            Else
                PrevName = ReleaseName
            End If

            PrevRelease = VendorRelease
            PrevCounter = Counter

        End While

        If Definition <> "" Then
            TmpCount = TmpCount + 1
            PrevName = PrevName & "_" & TmpCount
            'insert last
            Definition = Replace(Definition, "'", "''")
            exec_order += 1
            PrintLine(iFileNum, "insert into ExternalStatement " & _
            "(versionid,statementname, executionorder, dbconnection, statement) " & _
            "values (" & _
            "'" & PackRel & "'," & _
            "'insert into " & PrevName & "_' || dateformat(today(), 'yyyy-mm-dd')," & _
            exec_order & "," & _
            "'" & database & "'," & _
            "'" & Definition & "');")
        End If

        dbReader.Close()
        dbCommand.Dispose()
        Return exec_order

    End Function
    Private Function Initialize_Classes(ByRef logs As logMessages) As Boolean

        Dim Success As Boolean

        Try
            Dim tp_utils = New TPUtilities

            Dim DefaultMaxAmount As Integer

            TechPackName = tp_utils.readSingleValue("Coversheet$B2:B3", tpConn, dbCommand, dbReader)
            TechPackType = tp_utils.readSingleValue("Coversheet$B3:B4", tpConn, dbCommand, dbReader)
            Description = tp_utils.readSingleValue("Coversheet$B5:B6", tpConn, dbCommand, dbReader)
            TPReleaseVersion = tp_utils.readSingleValue("Coversheet$B6:B7", tpConn, dbCommand, dbReader)
            TPBuild = tp_utils.readSingleValue("Coversheet$C6:C7", tpConn, dbCommand, dbReader)
            ProductNumber = tp_utils.readSingleValue("Coversheet$B8:B9", tpConn, dbCommand, dbReader)
            VendorReleases = tp_utils.readSingleValue("Coversheet$B7:B8", tpConn, dbCommand, dbReader)

            'TPVersion = TPVersion & "_b(build)" '& TPBuild
            TPVersion = "b(build)" '& TPBuild

            DefaultKeyMaxAmount = 255
            DefaultCounterMaxAmount = 255
            'DefaultKeyMaxAmount = tp_utils.readSingleValue("Coversheet$B7:B8", baseConn, dbCommand, dbReader)
            'DefaultCounterMaxAmount = tp_utils.readSingleValue("Coversheet$B8:B9", baseConn, dbCommand, dbReader)
            'If DefaultCounterMaxAmount <> "" Then
            'DefaultMaxAmount = DefaultCounterMaxAmount
            'Else
            'DefaultMaxAmount = 255
            'logs.AddLogText("Default Counter Max Amount set to 255.")
            'End If


            If InStrRev(TPReleaseVersion, ",") > 0 OrElse InStrRev(TPReleaseVersion, ";") > 0 Then
                logs.AddLogText("TP Version can not contain following characters: ',', ';'.")
            End If

            If TechPackType = "CUSTOM" Then
                UseTechPackName = "CUSTOM_" & TechPackName
            Else
                UseTechPackName = TechPackName
            End If

            PackRel = UseTechPackName & ":" & TPVersion
            Version_Information = "insert into Versioning " & _
            "(versionid, description, status, techpack_name, techpack_version, techpack_type, product_number) " & _
            "values (" & _
            "'" & PackRel & "'," & _
            "'" & Description & " " & TPVersion & "'," & _
            1 & "," & _
            "'" & UseTechPackName & "'," & _
            "'" & TPReleaseVersion & "_b(build)'," & _
            "'" & TechPackType & "'," & _
            "'" & ProductNumber & "');" & Chr(10)

            'Read Counters sheet
            all_cnts = New Counters
            all_cnts.getCounters(DefaultCounterMaxAmount, tpConn, dbCommand, dbReader, logs)

            'Read Keys sheet
            all_cnt_keys = New CounterKeys
            all_cnt_keys.getCounterKeys(DefaultKeyMaxAmount, tpConn, dbCommand, dbReader, logs)

            'Read Public Keys Sheet
            pub_keys = New PublicKeys
            pub_keys.getPublicKeys(baseConn, dbCommand, dbReader, logs)


            Dim ObjectRow As Boolean
            all_bh_types = New BHTypes
            all_bhobjs = New BHObjects
            ObjectRow = False
            dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [BusyHours$]", tpConn)
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
                        bh_type.BHElements = dbReader.GetValue(6).ToString()
                        If InStrRev(bh_type.BHType, " ") > 0 Then
                            logs.AddLogText("Busy Hour '" & bh_type.BHType & "' contains space characters.")
                        End If
                        all_bh_types.AddItem(bh_type)
                    End If
                End If
            End While
            dbReader.Close()
            dbCommand.Dispose()


            'all_mts = New MeasurementTypes
            'all_mts.getAllMeasurements(tpConn, dbCommand, dbReader)

            'Read Measurements sheet and add keys and counters to measurements
            mts = New MeasurementTypes
            Success = mts.getMeasurements(TechPackName, tpConn, dbCommand, dbReader, all_cnts, all_cnt_keys, pub_keys, logs)
            If Success = False Then
                Return False
            End If


            'Read TopologyData sheet
            rts = New ReferenceTypes
            Success = rts.getTopology(TechPackName, tpConn, dbCommand, dbReader, mts, logs)
            If Success = False Then
                Return False
            End If
            Success = rts.getTopology(TechPackName, baseConn, dbCommand, dbReader, mts, logs)
            If Success = False Then
                Return False
            End If
            rts.getVectorTopology(mts)

            rds = New ReferenceDatas
            common_rds = New ReferenceDatas
            rds.getTopology(TechPackName, tpConn, dbCommand, dbReader, mts, logs, Nothing)
            rds.getTopology(TechPackName, baseConn, dbCommand, dbReader, mts, logs, rts)
            rds.getVectorTopology(mts, logs)


            'Read Webportal Configuration
            implementors = New WebPortalImplementors
            implementors.getImplementors(tpConn, dbCommand, dbReader, logs)

            Return True

        Catch e As Exception
            logs.AddLogText("Error: " & e.ToString())
            Return False
        End Try

    End Function

    Private Function Initialize_InterfaceClasses(ByRef logs As logMessages) As Integer

        Try
            Dim count As Integer
            Dim tag_count As Integer
            Dim TransformMTs() As String
            Dim Found As Boolean
            Dim tp_utils = New TPUtilities

            IntfTechPackName = tp_utils.readSingleValue("Coversheet$B2:B3", intfConn, dbCommand, dbReader)
            IntfTPReleaseVersion = tp_utils.readSingleValue("Coversheet$B3:B4", intfConn, dbCommand, dbReader)
            MeasurementInterfaceList = Split(tp_utils.readSingleValue("Coversheet$B4:B5", intfConn, dbCommand, dbReader), ",")
            TopologyInterfaceList = Split(tp_utils.readSingleValue("Coversheet$B5:B6", intfConn, dbCommand, dbReader), ",")
            TPBuild = tp_utils.readSingleValue("Coversheet$C6:C7", tpConn, dbCommand, dbReader)

            'IntfTPVersion = IntfTPVersion & "_b(build)" '& TPBuild
            IntfTPVersion = "b(build)" '& TPBuild

            IntfTechPackType = tp_utils.readSingleValue("Coversheet$B6:B7", tpConn, dbCommand, dbReader)

            If IntfTechPackType = "CUSTOM" Then
                UseIntfTechPackName = "CUSTOM_" & IntfTechPackName
            Else
                UseIntfTechPackName = IntfTechPackName
            End If

            IntfPackRel = UseIntfTechPackName & ":" & IntfTPVersion

            Interface_Version_Information = "insert into Versioning " & _
            "(versionid, description, status, techpack_name, techpack_version, techpack_type, product_number) " & _
            "values (" & _
            "'" & IntfPackRel & "'," & _
            "'" & "" & "'," & _
            1 & "," & _
            "'" & UseIntfTechPackName & "'," & _
            "'" & IntfTPReleaseVersion & "_b(build)'," & _
            "'" & IntfTechPackType & "');" & Chr(10)

            'Data Interfaces
            data_intfs = New DataInterfaces
            For count = 0 To UBound(MeasurementInterfaceList)
                data_intf = New DataInterfaces.DataInterface
                data_intf.InterfaceType = MeasurementInterfaceList(count)
                data_intf.InterfaceName = "INTF_" & IntfTechPackName & "_" & data_intf.InterfaceType & ":" & IntfTPVersion
                data_intf.Release = IntfTPVersion
                data_intf.Status = 1
                data_intf.Description = ""
                data_intfs.AddItem(data_intf)
            Next count
            For count = 0 To UBound(TopologyInterfaceList)
                data_intf = New DataInterfaces.DataInterface
                data_intf.InterfaceType = TopologyInterfaceList(count)
                data_intf.InterfaceName = "INTF_" & IntfTechPackName & "_" & data_intf.InterfaceType & ":" & IntfTPVersion
                data_intf.Release = IntfTPVersion
                data_intf.Status = 1
                data_intf.Description = ""
                data_intfs.AddItem(data_intf)
            Next count

            'Data format information for measurements
            data_fmts = New DataFormats
            For count = 0 To UBound(MeasurementInterfaceList)
                dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Measurements$]", intfConn)
                dbReader = dbCommand.ExecuteReader()
                While (dbReader.Read())
                    If dbReader.GetValue(0).ToString() = "" Then
                        Exit While
                    Else
                        Dim Tags() As String = Split(dbReader.GetValue(count + 1).ToString(), ",")
                        For tag_count = 0 To UBound(Tags)
                            data_fmt = New DataFormats.DataFormat
                            data_fmt.Measurement = dbReader.GetValue(0).ToString()
                            data_fmt.Release = IntfTPVersion
                            data_fmt.DataFormatType = MeasurementInterfaceList(count)
                            data_fmt.TagId = Tags(tag_count)
                            data_fmt.Description = "Default tags for " & data_fmt.Measurement & " in " & IntfPackRel & " with format " & data_fmt.DataFormatType & "."
                            data_fmt.VersionId = IntfPackRel
                            data_fmt.DataFormatId = IntfPackRel & ":" & data_fmt.Measurement & ":" & data_fmt.DataFormatType
                            data_fmt.TypeId = IntfPackRel & ":" & data_fmt.Measurement
                            data_fmt.FolderName = data_fmt.Measurement
                            data_fmts.AddItem(data_fmt)
                        Next tag_count
                    End If
                End While
                dbReader.Close()
                dbCommand.Dispose()
            Next count

            'Data item information for keys
            key_items = New DataItems
            For count = 0 To UBound(MeasurementInterfaceList)
                dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Keys$]", intfConn)
                dbReader = dbCommand.ExecuteReader()
                While (dbReader.Read())
                    If dbReader.GetValue(0).ToString() = "" Then
                        Exit While
                    Else
                        key_item = New DataItems.DataItem
                        key_item.DataFormatId = IntfPackRel & ":" & dbReader.GetValue(0).ToString() & ":" & MeasurementInterfaceList(count)
                        key_item.DataName = dbReader.GetValue(1).ToString()
                        key_item.Release = IntfTPVersion
                        key_item.DataId = dbReader.GetValue(count + 2).ToString()

                        For mt_count = 1 To mts.Count
                            mt = mts.Item(mt_count)
                            If mt.MeasurementTypeID = dbReader.GetValue(0).ToString() Then
                                For cnt_count = 1 To mt.Counters.Count
                                    cnt = mt.Counters.Item(cnt_count)
                                    If cnt.CounterType = "VECTOR" Then
                                        key_item.ProcessInstruction = "KEY"
                                        Exit For
                                    End If
                                Next cnt_count
                            End If
                        Next mt_count

                        key_items.AddItem(key_item)
                    End If
                End While
                dbReader.Close()
                dbCommand.Dispose()
            Next count

            For count = 0 To UBound(MeasurementInterfaceList)
                For mt_count = 1 To mts.Count
                    mt = mts.Item(mt_count)
                    For cnt_count = 1 To mt.Counters.Count
                        cnt = mt.Counters.Item(cnt_count)
                        If cnt.CounterType = "VECTOR" Then
                            key_item = New DataItems.DataItem
                            key_item.DataFormatId = IntfPackRel & ":" & mt.MeasurementTypeID & ":" & MeasurementInterfaceList(count)
                            key_item.DataName = "DCVECTOR_INDEX"
                            key_item.Release = IntfTPVersion
                            key_item.DataId = "DCVECTOR_INDEX"
                            key_items.AddItem(key_item)
                            Exit For
                        End If
                    Next cnt_count
                Next mt_count
            Next count

            'Data item information for counters
            data_items = New DataItems
            For count = 0 To UBound(MeasurementInterfaceList)
                dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Counters$]", intfConn)
                dbReader = dbCommand.ExecuteReader()
                While (dbReader.Read())
                    If dbReader.GetValue(0).ToString() = "" Then
                        Exit While
                    Else
                        For mt_count = 1 To mts.Count
                            mt = mts.Item(mt_count)
                            If mt.MeasurementTypeID = dbReader.GetValue(0).ToString() Then
                                Found = False
                                For cnt_count = 1 To mt.Counters.Count
                                    cnt = mt.Counters.Item(cnt_count)
                                    If cnt.CounterName = dbReader.GetValue(1).ToString() Then
                                        Found = True
                                        If cnt.CounterType = "VECTOR" Then
                                            data_item = New DataItems.DataItem
                                            data_item.DataFormatId = IntfPackRel & ":" & dbReader.GetValue(0).ToString() & ":" & MeasurementInterfaceList(count)
                                            data_item.DataName = dbReader.GetValue(1).ToString()
                                            data_item.Release = IntfTPVersion
                                            data_item.DataId = dbReader.GetValue(count + 2).ToString()
                                            data_item.ProcessInstruction = cnt.CounterProcess
                                            data_items.AddItem(data_item)
                                        Else
                                            data_item = New DataItems.DataItem
                                            data_item.DataFormatId = IntfPackRel & ":" & dbReader.GetValue(0).ToString() & ":" & MeasurementInterfaceList(count)
                                            data_item.DataName = dbReader.GetValue(1).ToString()
                                            data_item.Release = IntfTPVersion
                                            data_item.DataId = dbReader.GetValue(count + 2).ToString()
                                            data_items.AddItem(data_item)
                                        End If
                                    End If
                                Next cnt_count
                                If Found = False Then
                                    logs.AddLogText("Data item '" & dbReader.GetValue(1).ToString() & "' at Row " & cnt_count & " in Fact Table '" & mt.MeasurementTypeID & "' is not found in Fact Table Counters.")
                                End If
                            End If
                        Next mt_count

                    End If
                End While
                dbReader.Close()
                dbCommand.Dispose()
            Next count

            Dim intf_rts = New ReferenceTypes
            intf_rts.getTopology(TechPackName, tpConn, dbCommand, dbReader, mts, logs)


            'Data format information for topology
            topology_fmts = New DataFormats
            For count = 0 To UBound(TopologyInterfaceList)
                dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Reference tables$]", intfConn)
                dbReader = dbCommand.ExecuteReader()
                While (dbReader.Read())
                    If dbReader.GetValue(0).ToString() = "" Then
                        Exit While
                    Else
                        Dim Tags() As String = Split(dbReader.GetValue(count + 1).ToString(), ",")
                        For tag_count = 0 To UBound(Tags)
                            topology_fmt = New DataFormats.DataFormat
                            topology_fmt.Measurement = dbReader.GetValue(0).ToString()
                            topology_fmt.Release = IntfTPVersion
                            topology_fmt.DataFormatType = TopologyInterfaceList(count)
                            topology_fmt.TagId = Tags(tag_count)
                            topology_fmt.Description = "Default tags for " & topology_fmt.Measurement & " in " & IntfPackRel & " with format " & topology_fmt.DataFormatType & "."
                            topology_fmt.VersionId = IntfPackRel
                            topology_fmt.DataFormatId = IntfPackRel & ":" & topology_fmt.Measurement & ":" & topology_fmt.DataFormatType
                            topology_fmt.TypeId = IntfPackRel & ":" & topology_fmt.Measurement
                            topology_fmt.FolderName = topology_fmt.Measurement
                            topology_fmts.AddItem(topology_fmt)
                        Next tag_count
                    End If
                End While
                dbReader.Close()
                dbCommand.Dispose()
            Next count

            'Data format information for topology
            'topology_fmts = New DataFormats
            'For count = 0 To UBound(TopologyInterfaceList)
            'For rt_count = 1 To intf_rts.Count
            'rt = intf_rts.Item(rt_count)
            'If rt.Type = "table" Then
            'topology_fmt = New DataFormats.DataFormat
            'topology_fmt.Measurement = rt.ReferenceTypeID
            'topology_fmt.Release = IntfTPVersion
            'topology_fmt.DataFormatType = TopologyInterfaceList(count)
            'topology_fmt.TagId = rt.ReferenceTypeID
            'topology_fmt.Description = "Default tags for " & topology_fmt.Measurement & " in " & IntfPackRel & " with format " & topology_fmt.DataFormatType & "."
            'topology_fmt.VersionId = IntfPackRel
            'topology_fmt.DataFormatId = IntfPackRel & ":" & topology_fmt.Measurement & ":" & topology_fmt.DataFormatType
            'topology_fmt.TypeId = IntfPackRel & ":" & topology_fmt.Measurement
            'topology_fmt.FolderName = topology_fmt.Measurement
            'topology_fmts.AddItem(topology_fmt)
            'End If
            'Next rt_count
            'Next count

            'Data items for topology
            topology_items = New DataItems
            For count = 0 To UBound(TopologyInterfaceList)
                dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Topology$]", intfConn)
                dbReader = dbCommand.ExecuteReader()
                While (dbReader.Read())
                    If dbReader.GetValue(0).ToString() = "" Then
                        Exit While
                    Else
                        topology_item = New DataItems.DataItem
                        topology_item.DataFormatId = IntfPackRel & ":" & dbReader.GetValue(0).ToString() & ":" & TopologyInterfaceList(count)
                        topology_item.DataName = dbReader.GetValue(1).ToString()
                        topology_item.Release = IntfTPVersion
                        topology_item.DataId = dbReader.GetValue(count + 2).ToString()
                        topology_items.AddItem(topology_item)
                    End If
                End While
                dbReader.Close()
                dbCommand.Dispose()
            Next count

            'TODO: common data items for topology

            transforms = New Transformations
            'Fact Transformations
            For count = 0 To UBound(TopologyInterfaceList)
                dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Transformation$]", intfConn)
                dbReader = dbCommand.ExecuteReader()
                While (dbReader.Read())
                    If dbReader.GetValue(0).ToString() = "" Then
                        Exit While
                    Else
                        If LCase(dbReader.GetValue(1).ToString()) = "all" Then
                            For mt_count = 1 To mts.Count
                                mt = mts.Item(mt_count)
                                If mt.RankTable = False Then
                                    transform = New Transformations.Transformation
                                    transform.InterfaceName = dbReader.GetValue(0).ToString()
                                    transform.MeasurementTypeID = mt.MeasurementTypeID
                                    transform.TransformationId = IntfPackRel & ":" & transform.MeasurementTypeID & ":" & transform.InterfaceName
                                    transform.Type = dbReader.GetValue(2).ToString()
                                    transform.Source = dbReader.GetValue(3).ToString()
                                    transform.Target = dbReader.GetValue(4).ToString()
                                    transform.Config = dbReader.GetValue(5).ToString()
                                    transform.Release = IntfPackRel
                                    transforms.AddItem(transform)
                                End If
                            Next mt_count
                        Else
                            TransformMTs = Split(dbReader.GetValue(1).ToString(), ",")
                            For mt_count = 0 To UBound(TransformMTs)
                                transform = New Transformations.Transformation
                                transform.InterfaceName = dbReader.GetValue(0).ToString()
                                transform.MeasurementTypeID = TransformMTs(mt_count)
                                transform.TransformationId = IntfPackRel & ":" & transform.MeasurementTypeID & ":" & transform.InterfaceName
                                transform.Type = dbReader.GetValue(2).ToString()
                                transform.Source = dbReader.GetValue(3).ToString()
                                transform.Target = dbReader.GetValue(4).ToString()
                                transform.Config = dbReader.GetValue(5).ToString()
                                transform.Release = IntfPackRel
                                transforms.AddItem(transform)
                            Next mt_count
                        End If
                    End If
                End While
                dbReader.Close()
                dbCommand.Dispose()
            Next count

            'Dimension Transformations
            For count = 0 To UBound(TopologyInterfaceList)
                dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Dimension Transformation$]", intfConn)
                dbReader = dbCommand.ExecuteReader()
                While (dbReader.Read())
                    If dbReader.GetValue(0).ToString() = "" Then
                        Exit While
                    Else
                        If LCase(dbReader.GetValue(1).ToString()) = "all" Then
                            For rt_count = 1 To intf_rts.Count
                                rt = intf_rts.Item(rt_count)
                                If rt.Type = "table" Then
                                    transform = New Transformations.Transformation
                                    transform.InterfaceName = dbReader.GetValue(0).ToString()
                                    transform.MeasurementTypeID = rt.ReferenceTypeID
                                    transform.TransformationId = IntfPackRel & ":" & transform.MeasurementTypeID & ":" & transform.InterfaceName
                                    transform.Type = dbReader.GetValue(2).ToString()
                                    transform.Source = dbReader.GetValue(3).ToString()
                                    transform.Target = dbReader.GetValue(4).ToString()
                                    transform.Config = dbReader.GetValue(5).ToString()
                                    transform.Release = IntfPackRel
                                    transforms.AddItem(transform)
                                End If
                            Next rt_count
                        Else
                            TransformMTs = Split(dbReader.GetValue(1).ToString(), ",")
                            For rt_count = 0 To UBound(TransformMTs)
                                transform = New Transformations.Transformation
                                transform.InterfaceName = dbReader.GetValue(0).ToString()
                                transform.MeasurementTypeID = TransformMTs(rt_count)
                                transform.TransformationId = IntfPackRel & ":" & transform.MeasurementTypeID & ":" & transform.InterfaceName
                                transform.Type = dbReader.GetValue(2).ToString()
                                transform.Source = dbReader.GetValue(3).ToString()
                                transform.Target = dbReader.GetValue(4).ToString()
                                transform.Config = dbReader.GetValue(5).ToString()
                                transform.Release = IntfPackRel
                                transforms.AddItem(transform)
                            Next rt_count
                        End If
                    End If
                End While
                dbReader.Close()
                dbCommand.Dispose()
            Next count


            'InterfaceMeasurements
            intfMeasurements = New InterfaceMeasurements
            For count = 0 To UBound(MeasurementInterfaceList)
                dbCommand = New System.Data.OleDb.OleDbCommand("SELECT * FROM [Measurements$]", intfConn)
                dbReader = dbCommand.ExecuteReader()
                While (dbReader.Read())
                    If dbReader.GetValue(0).ToString() = "" Then
                        Exit While
                    Else
                        Dim Tags() As String = Split(dbReader.GetValue(count + 1).ToString(), ",")
                        For tag_count = 0 To UBound(Tags)
                            intfMeasurement = New InterfaceMeasurements.InterfaceMeasurement
                            intfMeasurement.TagId = Tags(tag_count)
                            intfMeasurement.Measurement = dbReader.GetValue(0).ToString()
                            intfMeasurement.DataFormatType = MeasurementInterfaceList(count)
                            intfMeasurement.DataFormatId = IntfPackRel & ":" & intfMeasurement.Measurement & ":" & intfMeasurement.DataFormatType
                            intfMeasurement.InterfaceName = "INTF_" & IntfTechPackName & "_" & intfMeasurement.DataFormatType & ":" & IntfTPVersion
                            intfMeasurement.TransformerId = IntfPackRel & ":" & intfMeasurement.Measurement & ":" & intfMeasurement.DataFormatType
                            intfMeasurement.Status = 1
                            intfMeasurement.Description = "Default tags for " & intfMeasurement.Measurement & " in " & IntfPackRel & " with format " & intfMeasurement.DataFormatType & "."
                            intfMeasurement.Release = IntfTPVersion
                            intfMeasurements.AddItem(intfMeasurement)
                        Next tag_count
                    End If
                End While
                dbReader.Close()
                dbCommand.Dispose()
            Next count
            For count = 0 To UBound(TopologyInterfaceList)
                For rt_count = 1 To intf_rts.Count
                    rt = intf_rts.Item(rt_count)
                    If rt.Type = "table" Then
                        intfMeasurement = New InterfaceMeasurements.InterfaceMeasurement
                        intfMeasurement.TagId = rt.ReferenceTypeID
                        intfMeasurement.Measurement = rt.ReferenceTypeID
                        intfMeasurement.DataFormatType = TopologyInterfaceList(count)
                        intfMeasurement.DataFormatId = IntfPackRel & ":" & intfMeasurement.Measurement & ":" & intfMeasurement.DataFormatType
                        intfMeasurement.InterfaceName = "INTF_" & IntfTechPackName & "_" & intfMeasurement.DataFormatType & ":" & IntfTPVersion
                        intfMeasurement.TransformerId = IntfPackRel & ":" & intfMeasurement.Measurement & ":" & intfMeasurement.DataFormatType
                        intfMeasurement.Status = 1
                        intfMeasurement.Description = "Default tags for " & intfMeasurement.Measurement & " in " & IntfPackRel & " with format " & intfMeasurement.DataFormatType & "."
                        intfMeasurement.Release = IntfTPVersion
                        intfMeasurements.AddItem(intfMeasurement)
                    End If
                Next rt_count
            Next count

            Return 0

        Catch e As Exception
            logs.AddLogText("Error: " & e.ToString())
            Return 1
        End Try

    End Function


    'Sub TechPack_SampleData(ByVal FilePrefix As String)

    'Dim iFileNum As Short
    'Dim samplefile As String
    'Dim sampledate As String
    'Dim toDate As Date

    'Dim sampleDays As Integer
    'Dim i As Integer

    'toDate = New Date(1999, 12, 6)

    'sampleDays = 42

    'For i = 0 To sampleDays - 1
    'toDate = toDate.AddDays(1)
    'For mt_count = 1 To mts.Count
    'mt = mts.Item(mt_count)
    'If toDate.Month < 10 Then
    'If toDate.Day < 10 Then
    'sampledate = toDate.Year & "-0" & toDate.Month & "-0" & toDate.Day
    'Else
    'sampledate = toDate.Year & "-0" & toDate.Month & "-" & toDate.Day
    'End If
    'Else
    'If toDate.Day < 10 Then
    'sampledate = toDate.Year & "-" & toDate.Month & "-0" & toDate.Day
    'Else
    'sampledate = toDate.Year & "-" & toDate.Month & "-" & toDate.Day
    'End If
    'End If

    'samplefile = FilePrefix & mt.MeasurementTypeID & "_sample_" & sampledate & ".txt"
    'iFileNum = FreeFile()
    'FileOpen(iFileNum, samplefile, OpenMode.Output)
    'mt.CreateSampleData(iFileNum, sampledate, toDate.Month, toDate.Day, i)
    'FileClose(iFileNum)
    'Next mt_count
    'Next

    'End Sub

    Protected Overrides Sub Finalize()
        MyBase.Finalize()
    End Sub

    Public Sub New()

    End Sub
End Class