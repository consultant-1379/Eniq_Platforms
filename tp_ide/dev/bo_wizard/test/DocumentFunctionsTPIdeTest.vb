Imports NUnit.Framework
Imports NMock2
Imports Designer
Imports System.Xml
Imports System.Xml.Xsl

<TestFixture()> _
Public Class DocumentFunctionsTPIdeTest

    Private testInstance As DocumentFunctionsForTest
    Private mocks As NMock2.Mockery

    <SetUp()> _
    Public Sub SetUp()
        mocks = New NMock2.Mockery()
    End Sub

    <TearDown()> _
    Public Sub TearDown()
        testInstance = Nothing
        Try
            mocks.VerifyAllExpectationsHaveBeenMet()
        Catch ex As Exception
            Console.WriteLine(ex.ToString())
        End Try
    End Sub

    <Test()> _
    Public Sub generateTechPackProductReferenceTest()
        ' Create new instance for test:
        Dim tpUtilities As TPUtilitiesTPIdeForTest = New TPUtilitiesTPIdeForTest()
        ' Create mocks:
        Dim mockDesignerApp As Designer.IApplication = mocks.NewMock(Of Designer.IApplication)()
        Dim mockUniverse As Designer.IUniverse = mocks.NewMock(Of Designer.IUniverse)()
        Dim mockDocumentWriter As IDocumentWriter = mocks.NewMock(Of IDocumentWriter)()

        tpUtilities.Universe = mockUniverse
        tpUtilities.DesignerApp = mockDesignerApp
        tpUtilities.UsersChoice = MsgBoxResult.Yes

        testInstance = New DocumentFunctionsForTest(tpUtilities, mockDocumentWriter)

        Dim Filename As String = "TP Ericsson BSS PM.unv"
        Dim OutputDir_Original As String = "outputDir"
        Dim CMTechPack As Boolean = False
        Dim BoUser As String = "user"
        Dim BoPass As String = "password"
        Dim BoRep As String = "atrcx886vm4:6400"
        Dim BoVersion As String = "XI"
        Dim BoAut As String = "ENTERPRISE"

        ' Expectations:
        Expect.Once.On(mockDesignerApp).SetProperty("Visible").To(False)

        Expect.Once.On(mockDesignerApp).Method("GetInstallDirectory").With(Designer.DsDirectoryID.dsDesignerDirectory)
        Expect.Once.On(mockDesignerApp).GetProperty("Version").Will(NMock2.Return.Value("test version"))

        Expect.Once.On(mockDesignerApp).Method("Logon").With(BoUser, BoPass, BoRep, BoAut)

        Expect.Once.On(mockDesignerApp).SetProperty("Interactive").To(False)

        Expect.Once.On(mockDocumentWriter).Method("generateTempXMLFile").With(OutputDir_Original & "\doc", CMTechPack, mockUniverse)
        Expect.Once.On(mockDocumentWriter).Method("generateSDIFFile").With(OutputDir_Original & "\doc", Nothing, mockUniverse)
        Expect.Once.On(mockDocumentWriter).Method("generateHTMLFile").With(OutputDir_Original & "\doc", Nothing, mockUniverse)

        Expect.Once.On(mockUniverse).GetProperty("LongName").Will(NMock2.Return.Value("TP Ericsson BSS PM"))
        Expect.Once.On(mockUniverse).Method("Close")

        Expect.Once.On(mockDesignerApp).SetProperty("Interactive").To(True)
        Expect.Once.On(mockDesignerApp).SetProperty("Visible").To(True)
        Expect.Once.On(mockDesignerApp).Method("Quit")
        Assert.IsTrue(testInstance.GenerateTechPackProductReference(Filename, OutputDir_Original, CMTechPack, BoUser, BoPass, BoRep, BoVersion, BoAut), _
                                                                    "Generating universe reference document should return true")
    End Sub

    Private Class DocumentFunctionsForTest
        Inherits DocumentFunctionsTPIde

        Public Sub New(ByVal utilities As TPUtilitiesTPIde, ByVal documentWriter As IDocumentWriter)
            MyBase.New(utilities, documentWriter)
        End Sub

        Protected Overrides Function createXslTransform() As XslTransform
            ' test returns a null value
            Return Nothing
        End Function

        Protected Overrides Sub createNewDirectory(ByVal OutputDir As String)
            ' does nothing in tests
        End Sub
    End Class

    ''
    'A test instance of UniverseFunctionsTPIde. 
    'This test class overrides displayMessageBox() so that it is not displayed when the unit tests are run.
    Private Class TPUtilitiesTPIdeForTest
        Inherits TPUtilitiesTPIde

        ' The user's choice when they click Yes or No for the message box.
        Private m_usersChoice As MsgBoxResult
        ' Universe
        Private m_Universe As Designer.IUniverse
        ' DesignerApp
        Private m_DesignerApp As Designer.IApplication

        ' Property method to get and set the m_usersChoice variable.
        ' Will be used by tests to set up 
        Public Property UsersChoice() As MsgBoxResult
            Get
                m_usersChoice = m_usersChoice
            End Get

            Set(ByVal choice As MsgBoxResult)
                m_usersChoice = choice
            End Set
        End Property

        ' Property method to get and set the m_usersChoice variable.
        ' Will be used by tests to set up 
        Public Property Universe() As Designer.IUniverse
            Get
                Universe = m_Universe
            End Get

            Set(ByVal universe As Designer.IUniverse)
                m_Universe = universe
            End Set
        End Property

        ' Property method to get and set the m_usersChoice variable.
        ' Will be used by tests to set up 
        Public Property DesignerApp() As Designer.IApplication
            Get
                DesignerApp = m_DesignerApp
            End Get

            Set(ByVal designerApp As Designer.IApplication)
                m_DesignerApp = designerApp
            End Set
        End Property

        ' Overridden version of displayMessageBox() to return m_usersChoice.
        ' Avoids displaying the message box when tests are run.
        Public Overrides Function displayMessageBox(ByVal message As String, ByVal msgBoxStyle As MsgBoxStyle, _
                                                       ByVal msgBoxTitle As String) As MsgBoxResult
            Return m_usersChoice
        End Function

        Protected Overrides Function doUniverseOpen(ByRef DesignerApp As Designer.IApplication) As Designer.IUniverse
            Return m_Universe
        End Function

        Protected Overrides Function createDesignerApp() As Designer.IApplication
            Return m_DesignerApp
        End Function
    End Class

End Class
