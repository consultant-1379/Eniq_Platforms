Option Strict Off
Option Explicit On

Imports System.Xml
Imports System.Xml.Xsl
Imports System.Xml.XPath
Imports System.IO
Imports System.Globalization
Imports System.Reflection.MethodBase

'require variables to be declared before being used
Public Class DocumentFunctionsTPIde

    Dim Univ As Designer.IUniverse

    Dim counterClass As BOClassesTPIde.BOClass

    Dim topologyClass As BOClassesTPIde.BOClass
    Dim topology_count As Integer

    Dim busyhourClass As BOClassesTPIde.BOClass

    Dim timeClass As BOClassesTPIde.BOClass
    Dim time_count As Integer

    Dim classObjects As BOObjectsTPIde
    Dim classObject As BOObjectsTPIde.BOObject

    'Dim classComputedObjects As BOComputedObjectTPIde
    'Dim classComputedObject As BOComputedObjectTPIde.BOComputedObject

    Dim classConditions As BOConditionsTPIde
    Dim classCondition As BOConditionsTPIde.BOCondition

    Dim contexts As BOContextsTPIde
    Dim context_join As BOContextsTPIde.BOJoin

    Dim boobject As BOObjectsTPIde.BOObject
    'Dim bocomputedobject As BOComputedObjectTPIde.BOComputedObject


    Dim bocondition As BOConditionsTPIde.BOCondition

    Dim bojoin_count As Integer

    Dim univ_name As String
    Dim univ_filename As String

    Dim mts As MeasurementTypesTPIde
    Dim rep_mts As MeasurementTypesTPIde
    Dim mt As MeasurementTypesTPIde.MeasurementType
    Dim all_cnts As CountersTPIde
    Dim cnts As CountersTPIde
    Dim cnt As CountersTPIde.Counter

    Dim all_cnt_keys As CounterKeysTPIde
    Dim cnt_key As CounterKeysTPIde.CounterKey

    Dim mt_count As Long
    Dim cnt_count As Long
    Dim cnt_key_count As Long


    Dim rts As ReferenceTypesTPIde

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

    Dim dbCommand As System.Data.Odbc.OdbcCommand
    Dim dbReader As System.Data.Odbc.OdbcDataReader

    Dim tpAdoConn As String

    Dim tpConn As System.Data.Odbc.OdbcConnection

    Dim tpreports As TPReports
    Dim tpreport As TPReports.TPReport
    Dim tpreport_count As Integer

    Dim tpkpis As TPKPIs
    Dim tpkpi As TPKPIs.TPKPI
    Dim tpkpi_count As Integer

    Private m_tpUtilities As TPUtilitiesTPIde
    Private m_documentWriter As IDocumentWriter
    Private className As String = "DocumentFunctionsTPIde.vb"

    ''
    'Constructor for DocumentFunctionsTPIde. TPUtilitiesTPIde instance is passed in.
    '@param utilities       An instance of TPUtilitiesTPIde.
    '@param documentWriter  An instance of IDocumentWriter, does the work of creating the output files.
    Public Sub New(ByVal utilities As TPUtilitiesTPIde, ByVal documentWriter As IDocumentWriter)
        Me.m_tpUtilities = utilities
        Me.m_documentWriter = documentWriter
    End Sub

    ' This subroutine is called when you click button
    Function GenerateTechPackProductReference(ByRef Filename As String, ByRef OutputDir_Original As String, ByRef CMTechPack As Boolean, _
                                              ByRef BoUser As String, ByRef BoPass As String, ByRef BoRep As String, ByRef BoVersion As String, _
                                              ByRef BoAut As String) As Boolean
        Dim classNameAndFunction As String = className & "," & GetCurrentMethod.Name & ": "
        Trace.WriteLine(classNameAndFunction & " entering")

        Dim DesignerApp As Designer.IApplication = Nothing
        Dim retry As Boolean
        Dim OutputDir As String

        ' Boolean flag to check if the universe reference generation succeeded:
        Dim success As Boolean = True

        Try
            Console.WriteLine("Generating Techpack Product Reference file")
            DesignerApp = m_tpUtilities.setupDesignerApp(BoVersion, BoUser, BoPass, BoRep, BoAut)
            Console.WriteLine("Logged on to Designer application")
            Console.WriteLine("Please open the universe for tech pack: " & Filename)
            Univ = m_tpUtilities.openUniverse(DesignerApp)
            Console.WriteLine("Opened universe successfully")

            ' GetClasses(Univ, CMTechPack)

            OutputDir = OutputDir_Original & "\doc"
            createNewDirectory(OutputDir)

            Console.WriteLine("Creating new reference file in directory: " & OutputDir)
            Trace.WriteLine("Generating temporary xml file")
            m_documentWriter.generateTempXMLFile(OutputDir, CMTechPack, Univ)

            Dim xslt As XslTransform = createXslTransform()
            m_documentWriter.generateSDIFFile(OutputDir, xslt, Univ)
            m_documentWriter.generateHTMLFile(OutputDir, xslt, Univ)
        Catch ex As Exception
            Console.WriteLine(ex.Message)
            Trace.WriteLine("Transform error: " & ex.Message & " " & ex.StackTrace)
        End Try


        If (success = True) Then
            m_tpUtilities.displayMessageBox("Created universe reference document: " & OutputDir & "\Universe Reference " & Univ.LongName & ".sdif" _
                                          , MsgBoxStyle.OkOnly, "Universe reference document")
        End If

        ' Delete the temporary file:
        deleteTempFile(OutputDir & "\temp.xml")

        Univ.Close()
        DesignerApp.Interactive = True
        DesignerApp.Visible = True
        DesignerApp.Quit()
        DesignerApp = Nothing

        Trace.WriteLine(classNameAndFunction & " exiting, success = " & success)
        Return success
    End Function

    ''
    'Creates a new XslTransform instance. Can be overridden by tests.
    '@returns   xslt    New instance of XslTransform.
    Protected Overridable Function createXslTransform() As XslTransform
        Dim xslt As New XslTransform
        Return xslt
    End Function

    ''
    'Creates a new output directory for the universe reference file.
    '@param OutputDir   The output directory to create.
    Protected Overridable Sub createNewDirectory(ByVal OutputDir As String)
        Try
            If Not System.IO.Directory.Exists(OutputDir) Then
                System.IO.Directory.CreateDirectory(OutputDir)
                Console.WriteLine("Created new directory: " & OutputDir)
            End If
        Catch ex As Exception
            Console.WriteLine("Create Directory '" & OutputDir & "' failed: " & ex.Message & " " & ex.StackTrace)
            Trace.WriteLine("Create Directory '" & OutputDir & "' failed: " & ex.Message & " " & ex.StackTrace)
        End Try
    End Sub

    Protected Overridable Sub deleteTempFile(ByVal OutputDir As String)
        Try
            If System.IO.File.Exists(OutputDir & "\temp.xml") Then
                'System.IO.File.Delete(OutputDir & "\temp.xml")
            End If
        Catch ex As Exception
            Console.WriteLine(ex.Message)
            MsgBox("Removing temporary file '" & OutputDir & "\temp.xml" & "' failed: " & ex.Message & " " & ex.StackTrace)
        End Try
    End Sub

End Class
