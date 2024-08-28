Imports System.Collections

Class RegressionTest

    Public Sub doTest(ByVal techPacks As ArrayList, ByVal create As Boolean, ByVal update As Boolean, ByVal reports As Boolean, ByVal reference As Boolean, _
                      ByVal createLinked As Boolean, ByVal updateLinked As Boolean, _
                      ByVal username As String, ByVal password As String, ByVal boServer As String, ByVal baseTechPack As String, ByVal outputDir As String, _
                      ByVal boVersion As String, _
                      ByVal authentication As String, _
                      ByVal dwhrepConnection As String, _
                      ByVal universes As SortedList)
        Dim operationFactory As IOperationFactory
        operationFactory = New RegressionTestOperationFactory

        For Each techPack As String In techPacks
            If (create) Then
                Try
                    Dim operation As AbstractOperation = operationFactory.createOperation("createUnv", username, password, boServer, techPack, False, _
                                                                                                          baseTechPack, outputDir, dwhrepConnection, boVersion, authentication, "ENIQ", "")
                    operation.execute()
                    Console.WriteLine("Finished creating universe for : " & techPack)
                Catch ex As Exception
                    Console.WriteLine("Error creating universe for " & techPack & ": " & ex.ToString())
                End Try
            End If
            If (update) Then
                Dim operation As AbstractOperation = operationFactory.createOperation("updateUnv", username, password, boServer, techPack, False, _
                                                                                      baseTechPack, outputDir, dwhrepConnection, boVersion, authentication, "ENIQ", "")
                operation.execute()
                Trace.Write("Finished updating universe for : " & techPack)
            End If
            If (reports) Then
                Dim BoApp As busobj.IApplication
                BoApp = New busobj.Application
                BoApp.Visible = False

                Console.WriteLine("bo server is: " & boServer)
                Dim operation As AbstractOperation = operationFactory.createOperation("createRep", username, password, boServer, techPack, False, _
                                                                                      baseTechPack, outputDir, dwhrepConnection, boVersion, authentication, "ENIQ", "")
                operation.execute()
                Trace.Write("Finished updating universe for : " & techPack)

            End If

            If (reference) Then
                'Dim docFunctions As DocumentFunctionsTPIde = New DocumentFunctionsTPIde
                'Dim success As Boolean = False
                'success = docFunctions.GenerateTechPackProductReference(techPack, outputDir, False, username, password, _
                '                                                        boServer, boVersion, authentication, techPack)
            End If

            If (createLinked) Then
                Dim TPInstallFunctionsClass As TPInstallFunctionsTPIDE = New TPInstallFunctionsTPIDE
                Dim universeIds As ArrayList = universes.Item(techPack)
                For Each universeId As String In universeIds
                    TPInstallFunctionsClass.CreateLinkedUniverse(username, password, boServer, universeId, "ENIQ", True, boVersion, authentication)
                Next
            End If

            If (updateLinked) Then
                Dim TPInstallFunctionsClass As TPInstallFunctionsTPIDE = New TPInstallFunctionsTPIDE
                TPInstallFunctionsClass.UpdateLinkedUniverse(username, password, boServer, "universe", "ENIQ", True, boVersion, authentication)
            End If
        Next
    End Sub

End Class
