

Option Strict Off
Option Explicit On 

Imports System.Xml
Imports System.Xml.Xsl

'require variables to be declared before being used
Public NotInheritable Class DocumentFunctions


    Dim Univ As Designer.Universe

    Dim counterClass As BOClasses.BOClass

    Dim topologyClass As BOClasses.BOClass
    Dim topology_count As Integer

    Dim busyhourClass As BOClasses.BOClass

    Dim timeClass As BOClasses.BOClass
    Dim time_count As Integer

    Dim classObjects As boobjects
    Dim classObject As boobjects.BOObject

    Dim classConditions As BOConditions
    Dim classCondition As BOConditions.BOCondition

    Dim contexts As BOContexts
    Dim context_join As BOContexts.BOJoin

    'Dim boobjects As boobjects
    Dim boobject As boobjects.BOObject

    Dim bocondition As BOConditions.BOCondition

    Dim bojoin_count As Integer

    Dim univ_name As String
    Dim univ_filename As String

    Dim mts As MeasurementTypes
    Dim rep_mts As MeasurementTypes
    Dim mt As MeasurementTypes.MeasurementType
    Dim all_cnts As Counters
    Dim cnts As Counters
    Dim cnt As Counters.Counter

    Dim all_cnt_keys As CounterKeys
    Dim cnt_key As CounterKeys.CounterKey

    Dim mt_count As Long
    Dim cnt_count As Long
    Dim cnt_key_count As Long


    Dim rts As ReferenceTypes

    Dim tp_name As String
    Dim tp_description As String
    Dim tp_vendor_release As String
    Dim tp_release As String
    Dim tp_version As String

    Dim all_bhobjs As BHObjects

    Dim bhobj As BHObjects.BHObject
    Dim all_bh_types As BHTypes

    Dim bh_type As BHTypes.BHType

    Dim bh_count As Long

    Dim dbCommand As System.Data.OleDb.OleDbCommand
    Dim dbReader As System.Data.OleDb.OleDbDataReader

    Dim tpAdoConn As String

    Dim tpConn As System.Data.OleDb.OleDbConnection

    Dim tpreports As TPReports
    Dim tpreport As TPReports.TPReport
    Dim tpreport_count As Integer

    Dim tpkpis As TPKPIs
    Dim tpkpi As TPKPIs.TPKPI
    Dim tpkpi_count As Integer
    ' This subroutine is called when you click button
    Function GenerateTechPackProductReference(ByRef Filename As String, ByRef OutputDir_Original As String, ByRef CMTechPack As Boolean, ByRef BOVersion As String) As logMessages
        Dim DesignerApp As Designer.Application
        Dim retry As Boolean
        Dim OutputDir As String

        Dim logs As logMessages = New logMessages

        ' open BO designer
        DesignerApp = New Designer.Application
        DesignerApp.Visible = False
        If BOVersion = "6.5" Then
            DesignerApp.LoginAs()
        ElseIf BOVersion = "XI" Then
            DesignerApp.LogonDialog()
        Else
            MsgBox("Invalid BO Version. Contact support.")
        End If

        retry = True
        While retry = True
            Try
                retry = False
                DesignerApp.Interactive = False
                Univ = DesignerApp.Universes.Open
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        ' GetClasses(Univ, CMTechPack)

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

        DocTPDescription(OutputXMLWriter, Univ)
        DocMeasurementsAndObjects(Univ, OutputXMLWriter, CMTechPack)
        GetObjectInfo(OutputXMLWriter, Univ, CMTechPack)
        GetConditionInfo(OutputXMLWriter, Univ, CMTechPack)

        OutputXMLWriter.WriteEndElement()
        System.Threading.Thread.Sleep(1000) ' Sleep for 1 second

        OutputXMLWriter.WriteEndDocument()
        OutputXMLWriter.Flush()
        OutputXMLWriter.Close()

        Dim xslt As New XslTransform

        Try
            xslt.Load(Application.StartupPath() & "\TP_Reference_SDIF.xslt")
            xslt.Transform(OutputDir & "\temp.xml", OutputDir & "\Universe Reference " & Univ.LongName & ".sdif", Nothing)
            xslt.Load(Application.StartupPath() & "\TP_Reference.xslt")
            xslt.Transform(OutputDir & "\temp.xml", OutputDir & "\Universe Reference " & Univ.LongName & ".html", Nothing)
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

        Univ.Close()
        DesignerApp.Interactive = True
        DesignerApp.Visible = True
        DesignerApp.Quit()
        DesignerApp = Nothing

        MsgBox("Run complete.")

        Return logs

    End Function

    Private Sub GetConditionInfo(ByRef OutputXMLWriter As XmlTextWriter, ByRef Univ As Designer.Universe, ByRef CMTechPack As Boolean)
        OutputXMLWriter.WriteStartElement("tp_conditions")
        PrintConditionInfo(OutputXMLWriter, Univ, Univ.Classes.FindClass("Time"))
        PrintConditionInfo(OutputXMLWriter, Univ, Univ.Classes.FindClass("Topology"))
        If CMTechPack = False Then
            Dim Cls As Designer.Class

            For Each Cls In Univ.Classes.FindClass("Counters").Classes
                PrintConditionInfo(OutputXMLWriter, Univ, Univ.Classes.FindClass("Counters"))
                Exit For
            Next
            For Each Cls In Univ.Classes.FindClass("Busy Hour").Classes
                PrintConditionInfo(OutputXMLWriter, Univ, Univ.Classes.FindClass("Busy Hour"))
                Exit For
            Next
            Try
                For Each Cls In Univ.Classes.FindClass("Computed Counters").Classes
                    PrintConditionInfo(OutputXMLWriter, Univ, Univ.Classes.FindClass("Computed Counters"))
                    Exit For
                Next
            Catch ex As Exception
            End Try
        Else
            PrintConditionInfo(OutputXMLWriter, Univ, Univ.Classes.FindClass("Parameters"))
        End If
        OutputXMLWriter.WriteEndElement()
    End Sub
    Private Sub GetObjectInfo(ByRef OutputXMLWriter As XmlTextWriter, ByRef Univ As Designer.Universe, ByRef CMTechPack As Boolean)
        OutputXMLWriter.WriteStartElement("tp_objects")
        PrintObjectInfo(OutputXMLWriter, Univ, Univ.Classes.FindClass("Time"))
        PrintObjectInfo(OutputXMLWriter, Univ, Univ.Classes.FindClass("Topology"))
        If CMTechPack = False Then
            Dim Cls As Designer.Class

            For Each Cls In Univ.Classes.FindClass("Counters").Classes
                PrintObjectInfo(OutputXMLWriter, Univ, Univ.Classes.FindClass("Counters"))
                Exit For
            Next
            For Each Cls In Univ.Classes.FindClass("Busy Hour").Classes
                PrintObjectInfo(OutputXMLWriter, Univ, Univ.Classes.FindClass("Busy Hour"))
                Exit For
            Next
            Try
                For Each Cls In Univ.Classes.FindClass("Computed Counters").Classes
                    PrintObjectInfo(OutputXMLWriter, Univ, Univ.Classes.FindClass("Computed Counters"))
                    Exit For
                Next
            Catch ex As Exception
            End Try
        Else
            PrintObjectInfo(OutputXMLWriter, Univ, Univ.Classes.FindClass("Parameters"))
        End If
        OutputXMLWriter.WriteEndElement()
    End Sub
    Private Sub PrintObjectInfo(ByRef OutputXMLWriter As XmlTextWriter, ByRef Univ As Designer.Universe, ByRef Cls As Designer.Class)
        Dim subCls As Designer.Class
        Dim Obj As Designer.Object
        Dim Description As String

        If Cls.Objects.Count > 0 OrElse Cls.Classes.Count > 0 Then
            OutputXMLWriter.WriteStartElement("class")
            OutputXMLWriter.WriteAttributeString("name", Cls.Name)
            OutputXMLWriter.WriteAttributeString("description", Cls.Description)

            For Each Obj In Cls.Objects
                If Obj.Show = True Then
                    Description = Replace(Obj.Description, ChrW(34), "'")
                    OutputXMLWriter.WriteStartElement("object")
                    OutputXMLWriter.WriteAttributeString("name", Obj.Name)
                    OutputXMLWriter.WriteAttributeString("description", Description)
                    OutputXMLWriter.WriteAttributeString("aggregation", Obj.AggregateFunction)
                    OutputXMLWriter.WriteAttributeString("select", Obj.Select)
                    OutputXMLWriter.WriteAttributeString("where", Obj.Where)
                    OutputXMLWriter.WriteEndElement()
                End If
            Next
            For Each subCls In Cls.Classes
                PrintObjectInfo(OutputXMLWriter, Univ, subCls)
            Next
            OutputXMLWriter.WriteEndElement()
        End If
    End Sub
    Private Sub PrintConditionInfo(ByRef OutputXMLWriter As XmlTextWriter, ByRef Univ As Designer.Universe, ByRef Cls As Designer.Class)
        Dim subCls As Designer.Class
        Dim Cond As Designer.PredefinedCondition
        Dim Description As String

        If Cls.PredefinedConditions.Count > 0 OrElse Cls.Classes.Count > 0 Then
            OutputXMLWriter.WriteStartElement("class")
            OutputXMLWriter.WriteAttributeString("name", Cls.Name)
            OutputXMLWriter.WriteAttributeString("description", Cls.Description)

            For Each Cond In Cls.PredefinedConditions
                Description = Replace(Cond.Description, ChrW(34), "'")
                OutputXMLWriter.WriteStartElement("object")
                OutputXMLWriter.WriteAttributeString("name", Cond.Name)
                OutputXMLWriter.WriteAttributeString("description", Description)
                OutputXMLWriter.WriteAttributeString("where", Cond.Where)
                OutputXMLWriter.WriteEndElement()
            Next
            For Each subCls In Cls.Classes
                PrintConditionInfo(OutputXMLWriter, Univ, subCls)
            Next
            OutputXMLWriter.WriteEndElement()
        End If
    End Sub
    Private Sub DocTPDescription(ByVal OutputXMLWriter As XmlTextWriter, ByRef Univ As Designer.Universe)
        OutputXMLWriter.WriteStartElement("tp_description")
        OutputXMLWriter.WriteAttributeString("name", Univ.LongName)
        OutputXMLWriter.WriteAttributeString("filename", Univ.Name)
        OutputXMLWriter.WriteAttributeString("description", Univ.Description)
        OutputXMLWriter.WriteAttributeString("product", "Product Number")
        OutputXMLWriter.WriteAttributeString("release", "R State")
        OutputXMLWriter.WriteEndElement()
    End Sub
    Private Sub DocClass(ByRef Univ As Designer.Universe, ByRef Cls As Designer.Class, ByRef compareText As String, ByRef OutputXMLWriter As XmlTextWriter, ByRef univ_joins As UnivJoins)
        Dim Added As String
        Dim Count As Integer
        Dim compareSelect As String
        Dim subCls As Designer.Class
        Dim Obj As Designer.Object
        Dim Jn As Designer.Join
        Dim univ_join As UnivJoins.UnivJoin

        Added = ""

        If Cls.Objects.Count > 0 OrElse Cls.Classes.Count > 0 Then
            OutputXMLWriter.WriteStartElement("class")
            OutputXMLWriter.WriteAttributeString("name", Cls.Name)
            OutputXMLWriter.WriteAttributeString("link", compareText)

            For Each Obj In Cls.Objects
                compareSelect = Obj.Select
                For Count = 1 To univ_joins.Count
                    univ_join = univ_joins.Item(Count)
                    If (InStrRev(compareSelect, univ_join.FirstTable) > 0 OrElse InStrRev(compareSelect, univ_join.SecondTable) > 0) _
                    AndAlso (InStrRev(univ_join.FirstTable, compareText) > 0 OrElse InStrRev(univ_join.SecondTable, compareText) > 0) Then
                        If InStrRev(Added, Obj.Name & ",") = 0 Then
                            OutputXMLWriter.WriteStartElement("object")
                            OutputXMLWriter.WriteAttributeString("name", Obj.Name)
                            OutputXMLWriter.WriteEndElement()
                            Added &= Obj.Name & ","
                        End If
                    End If
                Next
            Next
            For Each subCls In Cls.Classes
                DocClass(Univ, subCls, compareText, OutputXMLWriter, univ_joins)
            Next
            OutputXMLWriter.WriteEndElement()
        End If

    End Sub
    Private Sub DocMeasurementsAndObjects(ByRef Univ As Designer.Universe, ByVal OutputXMLWriter As XmlTextWriter, ByRef CMTechPack As Boolean)
        Dim Cls As Designer.Class
        Dim subCls As Designer.Class
        Dim Jn As Designer.Join
        Dim Added As String
        Dim clsName As String
        Added = ""
        Dim SearchClass As String
        If CMTechPack = True Then
            SearchClass = "Parameters"
        Else
            SearchClass = "Counters"
        End If

        Dim univ_joins As New UnivJoins
        Dim univ_join As UnivJoins.UnivJoin

        For Each Jn In Univ.Joins
            univ_join = New UnivJoins.UnivJoin
            univ_join.FirstTable = Jn.FirstTable.Name
            univ_join.SecondTable = Jn.SecondTable.Name
            univ_joins.AddItem(univ_join)
        Next


        OutputXMLWriter.WriteStartElement("tp_object_hierarchy")
        DocClass(Univ, Univ.Classes.FindClass("Time"), "RAW", OutputXMLWriter, univ_joins)
        DocClass(Univ, Univ.Classes.FindClass("Time"), "COUNT", OutputXMLWriter, univ_joins)
        DocClass(Univ, Univ.Classes.FindClass("Time"), "DAY", OutputXMLWriter, univ_joins)
        DocClass(Univ, Univ.Classes.FindClass("Time"), "DAYBH", OutputXMLWriter, univ_joins)
        'DocClass(Univ, Univ.Classes.FindClass("Time"), "ELEMBH", OutputXMLWriter)
        For Each Cls In Univ.Classes.FindClass(SearchClass).Classes
            For Each subCls In Cls.Classes
                clsName = Replace(subCls.Name, "_RAW", "")
                clsName = Replace(clsName, "_BH", "")
                If InStrRev(Added, clsName & ",") = 0 Then
                    DocClass(Univ, Univ.Classes.FindClass("Topology"), clsName, OutputXMLWriter, univ_joins)
                    DocClass(Univ, Univ.Classes.FindClass("Busy Hour"), clsName, OutputXMLWriter, univ_joins)
                    Added &= clsName & ","
                End If
            Next
        Next
        OutputXMLWriter.WriteEndElement()
    End Sub

End Class
