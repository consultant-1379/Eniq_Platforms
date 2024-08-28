Public Class RegressionTestOperationFactory
    Implements IOperationFactory

    Public Function createOperation(ByVal operation As String, ByVal bouser As String, ByVal bopass As String, ByVal borep As String, ByVal tpident As String, _
                                    ByVal cmTechPack As Boolean, ByVal baseident As String, ByVal outputFolder As String, _
                                    ByVal eniqConn As String, ByVal boVersion As String, ByVal boAut As String, _
                                    ByVal domain As String, ByVal universe As String) As AbstractOperation Implements IOperationFactory.createOperation
        Dim newOperation As AbstractOperation = Nothing

        If (operation = "createUnv") Then
            newOperation = New CreateUnvOpForRegressionTest("Create Universe", bouser, bopass, borep, tpident, False, baseident, outputFolder, eniqConn, _
                                           boVersion, boAut, domain, universe)
        ElseIf (operation = "updateUnv") Then
            newOperation = New UpdateUnvOpForRegressionTest("Update Universe", bouser, bopass, borep, tpident, False, baseident, outputFolder, eniqConn, _
                                           boVersion, boAut, domain, universe, False)
        ElseIf (operation = "updateEbsUnv") Then
            newOperation = New UpdateUnvOp("Update EBS Universe", bouser, bopass, borep, tpident, False, baseident, outputFolder, eniqConn, _
                                           boVersion, boAut, domain, universe, True)
        ElseIf (operation = "createRep") Then
            Console.WriteLine("bo rep is: " & borep)
            newOperation = New CreateRepOpForRegressionTest("Create Verification Reports", bouser, bopass, borep, tpident, False, baseident, outputFolder, _
                                           eniqConn, boVersion, boAut, domain, universe, False)
        ElseIf (operation = "createDoc") Then
            newOperation = New CreateDocOp("Create Universe Reference Document", bouser, bopass, borep, tpident, False, baseident, outputFolder, _
                                           eniqConn, boVersion, boAut, domain, universe)
        ElseIf (operation = "listDomain") Then
            newOperation = New GetDomainListOp("Get Repository Domain List", bouser, bopass, borep, tpident, False, baseident, outputFolder, _
                                                 eniqConn, boVersion, boAut, domain, universe)
        ElseIf (operation = "listUniverses") Then
            newOperation = New GetDomainUnvListOp("Get Domain Universe List", bouser, bopass, borep, tpident, False, baseident, outputFolder, _
                                                 eniqConn, boVersion, boAut, domain, universe)
        ElseIf (operation = "createLinkedUnv") Then
            newOperation = New LinkedUnvOp("Create Linked Universe", bouser, bopass, borep, tpident, False, baseident, outputFolder, _
                                                 eniqConn, boVersion, boAut, domain, universe)
        ElseIf (operation = "updateLinkedUnv") Then
            newOperation = New LinkedUnvOp("Update Linked Universe", bouser, bopass, borep, tpident, False, baseident, outputFolder, _
                                                 eniqConn, boVersion, boAut, domain, universe)
        ElseIf (operation = "listContext") Then
            newOperation = New ListUniversesOp("List contexts", bouser, bopass, borep, tpident, False, baseident, outputFolder, _
                                                 eniqConn, boVersion, boAut, domain, universe)
        Else
            Throw New Exception("Unknown command: " & operation)
        End If
        Return newOperation
    End Function

    ' 
    Private Class CreateUnvOpForRegressionTest
        Inherits CreateUnvOp

        Public Sub New(ByVal operationName As String, ByVal bouser As String, ByVal bopass As String, ByVal borep As String, _
                   ByVal tpident As String, ByVal cmTechPack As Boolean, ByVal baseident As String, ByVal outputFolder As String, _
                   ByVal eniqConn As String, ByVal boVersion As String, ByVal boAut As String, ByVal domain As String, ByVal universe As String)
            MyBase.New(operationName, bouser, bopass, borep, tpident, cmTechPack, baseident, outputFolder, eniqConn, boVersion, boAut, domain, universe)
        End Sub

        Protected Overrides Function createUniverseFunctions() As UniverseFunctionsTPIde
            Return New UniverseFunctionsTPIdeForTest(MyBase.m_outputFolder, False, MyBase.m_borep)
        End Function

        Protected Overrides Sub displayLogFileMessage()
            ' do nothing
        End Sub

        Public Overrides Function cleanup() As Boolean
            MyBase.universeFunctions.cleanup()
            MyBase.universeFunctions = Nothing
        End Function
    End Class

    Private Class UpdateUnvOpForRegressionTest
        Inherits UpdateUnvOp

        Public Sub New(ByVal operationName As String, ByVal bouser As String, ByVal bopass As String, ByVal borep As String, _
                   ByVal tpident As String, ByVal cmTechPack As Boolean, ByVal baseident As String, ByVal outputFolder As String, _
                   ByVal eniqConn As String, ByVal boVersion As String, ByVal boAut As String, ByVal domain As String, ByVal universe As String, _
                   ByVal ebsTechPack As Boolean)
            MyBase.New(operationName, bouser, bopass, borep, _
                   tpident, cmTechPack, baseident, outputFolder, _
                   eniqConn, boVersion, boAut, domain, universe, _
                   ebsTechPack)
        End Sub

        Protected Overrides Function createUniverseFunctions() As UniverseFunctionsTPIde
            Return New UniverseFunctionsTPIdeForTest(MyBase.m_outputFolder, False, MyBase.m_domain)
        End Function

        Protected Overrides Sub displayLogFileMessage()
            ' do nothing
        End Sub

        Public Overrides Function cleanup() As Boolean
            MyBase.universeFunctions.cleanup()
            MyBase.universeFunctions = Nothing
        End Function
    End Class


    Private Class CreateRepOpForRegressionTest
        Inherits CreateRepOp

        Public Sub New(ByVal operationName As String, ByVal bouser As String, ByVal bopass As String, ByVal borep As String, _
                   ByVal tpident As String, ByVal cmTechPack As Boolean, ByVal baseident As String, ByVal outputFolder As String, _
                   ByVal eniqConn As String, ByVal boVersion As String, ByVal boAut As String, ByVal domain As String, ByVal universe As String, _
                   ByVal ebsTechPack As Boolean)
            MyBase.New(operationName, bouser, bopass, borep, _
                   tpident, cmTechPack, baseident, outputFolder, _
                   eniqConn, boVersion, boAut, domain, universe)
        End Sub

        Protected Overrides Function createUniverseFunctions() As UniverseFunctionsTPIde
            Console.WriteLine("Domain is: " & m_borep)
            Return New UniverseFunctionsTPIdeForTest(MyBase.m_outputFolder, True, MyBase.m_borep)
        End Function

        Protected Overrides Sub displayLogFileMessage()
            ' do nothing
        End Sub

        Public Overrides Function cleanup() As Boolean
            MyBase.universeFunctions.cleanup()
            MyBase.universeFunctions = Nothing
        End Function
    End Class


    Private Class UniverseFunctionsTPIdeForTest
        Inherits UniverseFunctionsTPIde

        Private outputDir As String
        Private exportUniverse As Boolean
        Private boRep As String

        Public Sub New(ByVal outputDirectory As String, ByVal exportUniverse As Boolean, ByVal boRep As String)
            MyBase.New(New DatabaseProxy(), New TPUtilitiesTPIdeForTest(exportUniverse, boRep))
            Me.outputDir = outputDirectory
            Me.exportUniverse = exportUniverse
            Me.boRep = boRep
        End Sub

        Public Overrides Function createTPUtilities() As ITPUtilitiesTPIde
            Return New TPUtilitiesTPIdeForTest(exportUniverse, boRep)
        End Function

        Protected Overrides Function getUniverseSaveDirectory(ByVal originalDirectory As String) As String
            ' Should save into \updated_universes:
            Return Me.outputDir & "\updated_universes"
        End Function
    End Class


    Private Class TPUtilitiesTPIdeForTest
        Inherits TPUtilitiesTPIde

        Private exportUniverse As Boolean = True
        Private boRep As String

        Public Sub New(ByVal exportUniverse As Boolean, ByVal boRep As String)
            Console.WriteLine("In constructor" & boRep)
            Me.exportUniverse = exportUniverse
            Me.boRep = boRep
        End Sub

        ' Override function to open universe to get universe files locally: 
        Public Overrides Function promptToOpenUniverse(ByRef UniverseNameExtension As String, ByVal UniverseExtension As String, ByVal BoVersion As String, _
                                   ByRef DesignerApp As Designer.IApplication, ByVal UniverseName As String, _
                                   ByVal UniverseFileName As String, ByVal outputFolder As String) As Designer.IUniverse
            Trace.WriteLine("promptToOpenUniverse():" & " entering")
            Dim newUniverse As Designer.IUniverse = Nothing
            Dim retry As Boolean
            Dim name As String

            DesignerApp.Visible = False
            DesignerApp.Interactive = False

            Console.WriteLine("promptToOpenUniverse() test:" & "Prompting user to open universe")
            If UniverseNameExtension <> "" Then
                name = UniverseName & " " & UniverseNameExtension
            Else
                name = UniverseName
            End If

            Dim filename As String = outputFolder & "\unv\" & name & ".unv"
            Dim univ As Designer.IUniverse

            If (System.IO.File.Exists(filename)) Then
                Console.WriteLine("promptToOpenUniverse() exporting file:" & filename)

                Try
                    ' Also export the universe:
                    DesignerApp.Universes.Export("ENIQ", filename)
                Catch ex As Exception
                    Console.WriteLine(ex.ToString())
                End Try
                Console.WriteLine("Exported universe ok")
            End If

            ' Otherwise assume it's been exported already:
            Try
                Dim universeDir As String = DesignerApp.GetInstallDirectory(Designer.DsDirectoryID.dsUniverseDirectory)
                Dim domainDir As String = Replace(Me.boRep, ":", "_")
                domainDir = "@" + domainDir
                ' example file name: "C:\Users\eciacah\AppData\Roaming\Business Objects\Business Objects 12.0\Universes\@atrcx886vm4_6400\ENIQ\" & name & ".unv"
                Dim universeFile As String = universeDir & "\" & domainDir & "\ENIQ\" & name & ".unv"
                Console.WriteLine("Opening universe file:" & universeFile)
                univ = DesignerApp.Universes.Open(universeFile)
            Catch ex As Exception
                Console.WriteLine("Failed to open universe")
                Throw New Exception("Error opening universe")
            End Try

            Return univ
        End Function

        ' Override function to display message 
        Public Overrides Function displayMessageBox(ByVal message As String, ByVal msgBoxStyle As MsgBoxStyle, _
        ByVal msgBoxTitle As String) As MsgBoxResult
            Console.WriteLine(message)
            Return MsgBoxResult.Yes
        End Function
    End Class

End Class
