Imports System.Xml
Imports System.Xml.Xsl
Imports System.Xml.XPath
Imports System.IO
Imports System.Globalization
Imports System.Reflection.MethodBase

' Class to create the SDIF and HTML universe reference files.
Public Class UniverseDocumentWriter
    Implements IDocumentWriter

    Private className As String = "UniverseDocumentWriter"

    Private m_techpackID As String

    Public Sub New()

    End Sub

    Public Sub New(ByVal techpackID As String)
        m_techpackID = techpackID
    End Sub

    ''
    'Generate the HTML file.
    '@param OutputDir   The output directory to create the html file in.  
    Public Sub generateHTMLFile(ByVal OutputDir As String, ByVal xslt As System.Xml.Xsl.XslTransform, _
                                ByVal Univ As Designer.IUniverse) Implements IDocumentWriter.generateHTMLFile
        Trace.WriteLine("Writing .html file")
        xslt.Load(Application.StartupPath() & "\TP_Reference.xslt")
        xslt.Transform(OutputDir & "\temp.xml", OutputDir & "\Universe Reference " & Univ.LongName & ".html", Nothing)
        Trace.WriteLine("Wrote .html file")
        Console.WriteLine("Wrote .html file")
    End Sub

    ''
    'Generate the SDIF file.
    '@param OutputDir   The output directory to create the .sdif file in.    
    Public Sub generateSDIFFile(ByVal OutputDir As String, ByVal xslt As System.Xml.Xsl.XslTransform, _
                                ByVal Univ As Designer.IUniverse) Implements IDocumentWriter.generateSDIFFile
        Dim document As XmlDataDocument = createXmlDataDocument()
        document.Load(OutputDir & "\temp.xml")

        Trace.WriteLine("Writing .sdif file")
        xslt.Load(Application.StartupPath() & "\TP_Reference_SDIF.xslt")
        Dim stream As FileStream = New FileStream(OutputDir & "\Universe Reference " & Univ.LongName & ".sdif", FileMode.Create)
        Dim writer As New StreamWriter(stream)

        xslt.Transform(document, Nothing, writer, Nothing)
        writer.Close()
        Trace.WriteLine("Wrote .sdif file")
        Console.WriteLine("Wrote .sdif file")
    End Sub

    ''
    'Generate temporary XML file.
    '
    '@param OutputDir   The output directory to create the xml file in.
    '@param CMTechPack  True if this is a CM tech pack.
    Public Sub generateTempXMLFile(ByVal OutputDir As String, ByVal CMTechPack As Boolean, _
                                   ByVal Univ As Designer.IUniverse) Implements IDocumentWriter.generateTempXMLFile
        ' Create XmlTextWriter:
        Dim OutputXMLWriter As XmlTextWriter = createOutputXMLWriter(OutputDir & "\temp.xml")

        OutputXMLWriter.Formatting = Formatting.Indented
        OutputXMLWriter.Indentation = 4
        Dim subset As String
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
        Trace.WriteLine("Generated temporary xml file successfully")
    End Sub

    Private Sub GetConditionInfo(ByRef OutputXMLWriter As XmlTextWriter, ByRef Univ As Designer.Universe, ByRef CMTechPack As Boolean)
        Dim classNameAndFunction As String = className & "," & GetCurrentMethod.Name & ": "
        Trace.WriteLine(classNameAndFunction & " entering")

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
        Trace.WriteLine(classNameAndFunction & " exiting")
    End Sub
    Private Sub GetObjectInfo(ByRef OutputXMLWriter As XmlTextWriter, ByRef Univ As Designer.Universe, ByRef CMTechPack As Boolean)
        Dim classNameAndFunction As String = className & "," & GetCurrentMethod.Name & ": "
        Trace.WriteLine(classNameAndFunction & " entering")

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
        Trace.WriteLine(classNameAndFunction & " exiting")
    End Sub
    Private Sub PrintObjectInfo(ByRef OutputXMLWriter As XmlTextWriter, ByRef Univ As Designer.Universe, ByRef Cls As Designer.Class)
        Dim subCls As Designer.Class
        Dim Obj As Designer.Object
        Dim Description As String

        If Cls.Objects.Count > 0 OrElse Cls.Classes.Count > 0 Then
            OutputXMLWriter.WriteStartElement("class")
            'OutputXMLWriter.WriteAttributeString("name", Cls.Name)
            'OutputXMLWriter.WriteAttributeString("description", Cls.Description)

	    If (Cls.Name.Contains("Counters") And m_techpackID.Contains("BULK_CM")) Then
                OutputXMLWriter.WriteAttributeString("name", Cls.Name.Replace("Counters", "Attributes"))
            Else
                OutputXMLWriter.WriteAttributeString("name", Cls.Name)
            End If

	    If (Cls.Name.Contains("Counters") And m_techpackID.Contains("BULK_CM")) Then
                OutputXMLWriter.WriteAttributeString("description", Cls.Description.Replace("Counters", "Attributes"))
            Else
                OutputXMLWriter.WriteAttributeString("description", Cls.Description)
            End If
 

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
            'OutputXMLWriter.WriteAttributeString("name", Cls.Name)
            'OutputXMLWriter.WriteAttributeString("description", Cls.Description)

	    If (Cls.Name.Contains("Counters") And m_techpackID.Contains("BULK_CM")) Then
                OutputXMLWriter.WriteAttributeString("name", Cls.Name.Replace("Counters", "Attributes"))
            Else
                OutputXMLWriter.WriteAttributeString("name", Cls.Name)
            End If

	    If (Cls.Name.Contains("Counters") And m_techpackID.Contains("BULK_CM")) Then
                OutputXMLWriter.WriteAttributeString("description", Cls.Description.Replace("Counters", "Attributes"))
            Else
                OutputXMLWriter.WriteAttributeString("description", Cls.Description)
            End If
	

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
        Try
            ' Modified Code - HL18305 -starts
            OutputXMLWriter.WriteStartElement("tp_description")
            OutputXMLWriter.WriteAttributeString("name", Univ.LongName)
            OutputXMLWriter.WriteAttributeString("filename", Univ.Name)
            OutputXMLWriter.WriteAttributeString("description", Univ.Description)

            Dim i As String
            i = Mid(Univ.Description, InStr(Univ.Description, "Product:"), 20)
            OutputXMLWriter.WriteAttributeString("product", Mid(i, 9))
            OutputXMLWriter.WriteAttributeString("release", Right(Univ.Description, 3))

            Dim modDate As Date = getUniverseModDate(Univ.ModificationDate)
            OutputXMLWriter.WriteAttributeString("modyear", modDate.Year())
            OutputXMLWriter.WriteAttributeString("modmonth", modDate.Month())
            OutputXMLWriter.WriteAttributeString("moddate", modDate.Day())
            ' Modified Code - HL18305 -ends -XSUBAYY
            OutputXMLWriter.WriteEndElement()
        Catch ex As Exception
            Trace.WriteLine("Error writing tech pack description to universe reference file: " & ex.ToString())
        End Try
    End Sub

    ''
    'Gets the universe ModificationDate parameter and converts it to a Date object.
    '@param dateString The date string read from the universe parameter 'ModificationDate'
    '@returns modDate A Date object representing the date.
    Public Function getUniverseModDate(ByVal dateString As String) As Date
        Dim classNameAndFunction As String = className & "," & GetCurrentMethod.Name & ": "
        Trace.WriteLine(classNameAndFunction & " entering")

        Dim modDate As Date
        Dim gotDateOk As Boolean = False
        Try
            ' Try to parse the date using Swedish format:
            gotDateOk = DateTime.TryParse(dateString, New CultureInfo("sv-SE"), DateTimeStyles.None, modDate)
            If (gotDateOk = False) Then
                ' Try to parse the date using en-US format:
                gotDateOk = DateTime.TryParse(dateString, New CultureInfo("en-US"), DateTimeStyles.None, modDate)
            End If
        Catch ex As Exception
            Trace.WriteLine("Failed to parse date string: " & dateString & ", using current date.")
        End Try

        If (gotDateOk = False) Then
            ' Use current date:
            modDate = Now()
        End If
        Trace.WriteLine("getUniverseModDate(), got date: " & modDate.ToString())
        Trace.WriteLine(classNameAndFunction & " exiting")
        Return modDate
    End Function

    Private Sub DocClass(ByRef Univ As Designer.Universe, ByRef Cls As Designer.Class, ByRef compareText As String, ByRef OutputXMLWriter As XmlTextWriter, ByRef univ_joins As UnivJoinsTPIde)
        Dim Added As String
        Dim Count As Integer
        Dim compareSelect As String
        Dim subCls As Designer.Class
        Dim Obj As Designer.Object
        Dim Jn As Designer.Join
        Dim univ_join As UnivJoinsTPIde.UnivJoin

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
                            'OutputXMLWriter.WriteAttributeString("name", Obj.Name)

	          	    If (Cls.Name.Contains("Counters") And m_techpackID.Contains("BULK_CM")) Then
                                OutputXMLWriter.WriteAttributeString("name", Cls.Name.Replace("Counters", "Attributes"))
                            Else
                                OutputXMLWriter.WriteAttributeString("name", Obj.Name)
                            End If
	
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
        Dim classNameAndFunction As String = className & "," & GetCurrentMethod.Name & ": "
        Trace.WriteLine(classNameAndFunction & " entering")

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

        Dim univ_joins As New UnivJoinsTPIde
        Dim univ_join As UnivJoinsTPIde.UnivJoin

        For Each Jn In Univ.Joins
            univ_join = New UnivJoinsTPIde.UnivJoin
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
        Trace.WriteLine(classNameAndFunction & " exiting")
    End Sub

    ''
    'Creates a new XmlTextWriter.
    '@returns   document A new instance of XmlDataDocument
    Protected Overridable Function createXmlDataDocument() As XmlDataDocument
        Dim document As New XmlDataDocument
        Return document
    End Function

    ''
    'Creates a new XmlTextWriter.
    '@param     outputFile      The output file to use for the XmlTextWriter.
    '@returns   OutputXMLWriter A new instance of XmlTextWriter
    Protected Overridable Function createOutputXMLWriter(ByVal outputFile As String) As XmlTextWriter
        Dim OutputXMLWriter As New XmlTextWriter(outputFile, New System.Text.UTF8Encoding(False))
        Return OutputXMLWriter
    End Function


End Class

