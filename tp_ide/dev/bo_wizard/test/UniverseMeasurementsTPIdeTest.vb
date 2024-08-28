Imports NUnit.Framework

<TestFixture()> _
Public Class UniverseMeasurementsTPIdeTest

    Private universeMeasurementsTPIde As UniverseMeasurementsTPIde

    <SetUp()> _
    Public Sub SetUp()

    End Sub

    <TearDown()> _
    Public Sub TearDown()
        universeMeasurementsTPIde = Nothing
    End Sub

    <Test()> _
    Public Sub getMTsForUniverseTest()
        universeMeasurementsTPIde = New UniverseMeasurementsTPIde()

        ' Set up a dummy measurement type:
        Dim measTypeA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeA.ExtendedUniverse = "a"

        Dim measTypeB As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeB.ExtendedUniverse = "b"

        Dim measTypeALL As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeALL.ExtendedUniverse = "all"

        Dim measTypeEMPTY As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeEMPTY.ExtendedUniverse = ""

        Dim fullListOfMTypes As MeasurementTypesTPIde = New MeasurementTypesTPIde
        fullListOfMTypes.AddItem(measTypeA)
        fullListOfMTypes.AddItem(measTypeB)
        fullListOfMTypes.AddItem(measTypeALL)
        fullListOfMTypes.AddItem(measTypeEMPTY)

        Dim measurementTypes As MeasurementTypesTPIde
        measurementTypes = universeMeasurementsTPIde.getMTsForUniverse(fullListOfMTypes, "a")
        Assert.IsTrue(measurementTypes.MeasurementTypes().Contains(measTypeA), _
                      "List of measurement types should contain measurement types with extension 'a'")

        measurementTypes = universeMeasurementsTPIde.getMTsForUniverse(fullListOfMTypes, "a")
        Assert.IsTrue(universeMeasurementsTPIde.getMTsForUniverse(fullListOfMTypes, "a").MeasurementTypes().Contains(measTypeALL), _
                      "List of measurement types should contain measurement types with extension 'all'")

        measurementTypes = universeMeasurementsTPIde.getMTsForUniverse(fullListOfMTypes, "")
        Assert.IsTrue(universeMeasurementsTPIde.getMTsForUniverse(fullListOfMTypes, "").MeasurementTypes().Contains(measTypeEMPTY), _
                      "List of measurement types should contain measurement types with blank extension ''")

        measurementTypes = universeMeasurementsTPIde.getMTsForUniverse(fullListOfMTypes, "a")
        Assert.IsFalse(universeMeasurementsTPIde.getMTsForUniverse(fullListOfMTypes, "a").MeasurementTypes().Contains(measTypeB), _
                      "List of measurement types should not contain measurement types with extension 'b'")
    End Sub

    <Test()> _
    Public Sub singleExtensionMatchesTest()
        universeMeasurementsTPIde = New UniverseMeasurementsTPIde()

        Assert.IsTrue(universeMeasurementsTPIde.singleExtensionMatches("a", "a"))

        Assert.IsFalse(universeMeasurementsTPIde.singleExtensionMatches("a", "b"))

        Assert.IsFalse(universeMeasurementsTPIde.singleExtensionMatches("a,b,c,d", "b"), "List of extensions should return false")

        Assert.IsFalse(universeMeasurementsTPIde.singleExtensionMatches("a,b,c,d", "e"), "List of extensions should return false")

        Assert.IsFalse(Nothing, Nothing)
    End Sub

    <Test()> _
    Public Sub listExtensionMatchesTest()
        universeMeasurementsTPIde = New UniverseMeasurementsTPIde()

        Assert.IsTrue(universeMeasurementsTPIde.listExtensionMatches("a", "a"))

        Assert.IsFalse(universeMeasurementsTPIde.listExtensionMatches("a", "b"))

        Assert.IsTrue(universeMeasurementsTPIde.listExtensionMatches("a,b,c,d", "b"), "List of extensions should return true")

        Assert.IsFalse(universeMeasurementsTPIde.listExtensionMatches("a,b,c,d", "e"), "List of extensions without value should return false")

        Assert.IsFalse(Nothing, Nothing)
    End Sub

    <Test()> _
    Public Sub getUniverseExtensionTest()
        universeMeasurementsTPIde = New UniverseMeasurementsTPIde()

        ' Takes a single value and gets the sub values from it:
        Dim returnValue As String = universeMeasurementsTPIde.getUniverseExtension("a=Standard", True)
        Assert.IsTrue(returnValue.Equals("a"), "Getting extension for name should return 'a'")

        returnValue = universeMeasurementsTPIde.getUniverseExtension("a=Standard", False)
        Assert.IsTrue(returnValue.Equals("Standard"), "Getting extension should return 'Standard'")

        returnValue = universeMeasurementsTPIde.getUniverseExtension("A test value without equals sign", False)
        Assert.IsTrue(returnValue.Equals(""), "Getting extension should return empty string")

        returnValue = universeMeasurementsTPIde.getUniverseExtension(("=".Split(","))(0), False)
        Assert.IsTrue(returnValue.Equals(""), "Getting extension should return empty string")

        returnValue = universeMeasurementsTPIde.getUniverseExtension("", False)
        Assert.IsTrue(returnValue.Equals(""), "Getting extension should return empty string")
    End Sub



    ''
    'Private class for createUniverseMeasurementTest.
    '@remarks Overrides functions that should not be executed in tests.
    Private Class UniverseMeasurementsTPIdeForCreateUnvMt
        Inherits UniverseMeasurementsTPIde

        Public Sub New()
            ' dummy constructor
        End Sub

        Public Overrides Function createListOfJoins(ByVal measTypes As MeasurementTypesTPIde, ByVal UniverseNamextension As String) As UnivJoinsTPIde
            Return New UnivJoinsTPIde()
        End Function

        Public Overrides Function getReferenceTypes(ByVal measTypes As MeasurementTypesTPIde) As ReferenceTypesTPIde
            Return New ReferenceTypesTPIde
        End Function

        Public Overrides Function getReferenceDatas(ByVal measTypes As MeasurementTypesTPIde) As ReferenceDatasTPIde
            Return New ReferenceDatasTPIde
        End Function

        Public Overrides Function getVectorReferenceTypes(ByVal measTypes As MeasurementTypesTPIde) As ReferenceTypesTPIde
            Return New ReferenceTypesTPIde
        End Function

        Public Overrides Function getVectorReferenceDatas(ByVal measTypes As MeasurementTypesTPIde) As ReferenceDatasTPIde
            Return New ReferenceDatasTPIde
        End Function
    End Class

    <Test()> _
    Public Sub createUniverseMeasurementHasCorrectTypes()
        ' Check the universe measurement has the correct measurement types:
        Dim univMeasTPIde As UniverseMeasurementsTPIdeForCreateUnvMt = New UniverseMeasurementsTPIdeForCreateUnvMt

        ' Create some individual measurement types with different universe extensions:
        Dim measTypeForA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeForA.ExtendedUniverse = "A"
        measTypeForA.TypeName = "typeA1"
        Dim measTypeForA2 As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeForA2.ExtendedUniverse = "A"
        measTypeForA.TypeName = "typeA2"
        Dim measTypeB As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeB.ExtendedUniverse = "B"
        measTypeForA.TypeName = "typeB"
        Dim measTypeC As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeC.ExtendedUniverse = "C"
        measTypeForA.TypeName = "typeC"
        Dim measTypeD As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeD.ExtendedUniverse = "D"
        measTypeForA.TypeName = "typeD"

        ' Create a dummy MeasurementTypesTPIde (holds the MeasurementType objects):
        Dim fullListOfMeasTypes As MeasurementTypesTPIde = New MeasurementTypesTPIde
        fullListOfMeasTypes.AddItem(measTypeForA)
        fullListOfMeasTypes.AddItem(measTypeForA2)
        fullListOfMeasTypes.AddItem(measTypeB)
        fullListOfMeasTypes.AddItem(measTypeC)
        fullListOfMeasTypes.AddItem(measTypeD)

        ' Call createUniverseMeasurement()
        ' The return value will be a single UniverseMeasurement that has a MeasurementTypesTPIde:
        Dim unvMeasurement As UniverseMeasurementsTPIde.UniverseMeasurement
        unvMeasurement = univMeasTPIde.createUniverseMeasurement("Standard", "a", fullListOfMeasTypes)

        ' Check the measurement types within MeasurementTypesTPIde:
        Assert.IsTrue(unvMeasurement.MeasurementTypes.MeasurementTypes.Contains(measTypeForA), "Universe measurement should only contain measurements from type A")
        Assert.IsTrue(unvMeasurement.MeasurementTypes.MeasurementTypes.Contains(measTypeForA2), "Universe measurement should only contain measurements from type A")
        Assert.IsTrue(unvMeasurement.MeasurementTypes.MeasurementTypes.ToArray.Length = 2, "Universe measurement should contain two measurements from type A")

        Assert.IsFalse(unvMeasurement.MeasurementTypes.MeasurementTypes.Contains(measTypeB), "Universe measurement should not contain measurement type B")
        Assert.IsFalse(unvMeasurement.MeasurementTypes.MeasurementTypes.Contains(measTypeC), "Universe measurement should not contain measurement type C")
        Assert.IsFalse(unvMeasurement.MeasurementTypes.MeasurementTypes.Contains(measTypeD), "Universe measurement should not contain measurement type D")
    End Sub

    <Test()> _
    Public Sub createUniverseMeasurementsCorrectNumberTest()
        ' Create some individual measurement types with different universe extensions:
        Dim measTypeForA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeForA.ExtendedUniverse = "A"
        Dim measTypeForA2 As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeForA2.ExtendedUniverse = "A"
        Dim measTypeB As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeB.ExtendedUniverse = "B"
        Dim measTypeC As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeC.ExtendedUniverse = "C"
        Dim measTypeD As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeD.ExtendedUniverse = "D"

        ' Create a dummy MeasurementTypesTPIde (holds the MeasurementType objects):
        Dim fullListOfMeasTypes As MeasurementTypesTPIde = New MeasurementTypesTPIde
        fullListOfMeasTypes.AddItem(measTypeForA)
        fullListOfMeasTypes.AddItem(measTypeForA2)
        fullListOfMeasTypes.AddItem(measTypeB)
        fullListOfMeasTypes.AddItem(measTypeC)
        fullListOfMeasTypes.AddItem(measTypeD)

        Dim univMeasTPIde As UniverseMeasurementsTPIdeForCreateUnvMt = New UniverseMeasurementsTPIdeForCreateUnvMt
        univMeasTPIde.createUniverseMeasurements(New String() {"a=Standard", "b=Extended", "c=WRAN", "d=TDRAN"}, fullListOfMeasTypes)
        Assert.IsTrue(univMeasTPIde.Count = 4, "The number of universes in UniverseMeasurementsTPIde should be 4")

        univMeasTPIde = New UniverseMeasurementsTPIdeForCreateUnvMt
        univMeasTPIde.createUniverseMeasurements(New String() {"c=WRAN", "d=TDRAN"}, fullListOfMeasTypes)
        Assert.IsTrue(univMeasTPIde.Count = 2, "The number of universes in UniverseMeasurementsTPIde should be 2")
    End Sub

    <Test()> _
    Public Sub createUniverseMeasurementsTest()
        Dim univMeasTPIde As UniverseMeasurementsTPIdeForCreateUnvMt = New UniverseMeasurementsTPIdeForCreateUnvMt

        ' Create some individual measurement types with different universe extensions:
        Dim measTypeForA As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeForA.ExtendedUniverse = "A"
        Dim measTypeForA2 As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeForA2.ExtendedUniverse = "A"
        Dim measTypeB As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeB.ExtendedUniverse = "B"
        Dim measTypeC As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeC.ExtendedUniverse = "C"
        Dim measTypeD As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measTypeD.ExtendedUniverse = "D"

        ' Create a dummy MeasurementTypesTPIde (holds the MeasurementType objects):
        Dim fullListOfMeasTypes As MeasurementTypesTPIde = New MeasurementTypesTPIde
        fullListOfMeasTypes.AddItem(measTypeForA)
        fullListOfMeasTypes.AddItem(measTypeForA2)
        fullListOfMeasTypes.AddItem(measTypeB)
        fullListOfMeasTypes.AddItem(measTypeC)
        fullListOfMeasTypes.AddItem(measTypeD)

        univMeasTPIde.createUniverseMeasurements(New String() {"a=Standard", "b=Extended", "c=WRAN", "d=TDRAN"}, _
                                                                                   fullListOfMeasTypes)

        ' First UniverseMeasurementsTPIde.UniverseMeasurement should have MTs with extension 'a':
        Assert.IsTrue(univMeasTPIde.Item(1).MeasurementTypes.MeasurementTypes.Contains(measTypeForA), _
                      "UniverseMeasurement for 'a' should only contain 'a' measurement types")
        Assert.IsTrue(univMeasTPIde.Item(1).MeasurementTypes.MeasurementTypes.Contains(measTypeForA2), _
                      "UniverseMeasurement for 'a' should only contain 'a' measurement type")
        Assert.IsFalse(univMeasTPIde.Item(1).MeasurementTypes.MeasurementTypes.Contains(measTypeB), _
                       "UniverseMeasurement for 'a' should only contain 'a' measurement types, not 'b' measurement types")

        Assert.IsTrue(univMeasTPIde.Item(2).MeasurementTypes.MeasurementTypes.Contains(measTypeB), _
                      "UniverseMeasurement for 'b' should only contain 'b' measurement type")
        Assert.IsFalse(univMeasTPIde.Item(2).MeasurementTypes.MeasurementTypes.Contains(measTypeForA), _
                       "UniverseMeasurement for 'b' should only contain 'b' measurement type, but not 'a' measurement types")
        Assert.IsFalse(univMeasTPIde.Item(2).MeasurementTypes.MeasurementTypes.Contains(measTypeForA2), _
                       "UniverseMeasurement for 'b' should only contain 'b' measurement type, but not 'a' measurement types")

        Assert.IsTrue(univMeasTPIde.Item(3).MeasurementTypes.MeasurementTypes.Contains(measTypeC), _
                      "UniverseMeasurement for 'c' should only contain 'c' measurement types")
        Assert.IsFalse(univMeasTPIde.Item(3).MeasurementTypes.MeasurementTypes.Contains(measTypeForA), _
                       "UniverseMeasurement for 'c' should only contain 'c' measurement type, but not 'a' measurement types")
        Assert.IsFalse(univMeasTPIde.Item(3).MeasurementTypes.MeasurementTypes.Contains(measTypeB), _
                       "UniverseMeasurement for 'c' should only contain 'c' measurement type, but not 'a' measurement types")

        Assert.IsTrue(univMeasTPIde.Item(4).MeasurementTypes.MeasurementTypes.Contains(measTypeD), _
                      "UniverseMeasurement for 'd' should contain 'd' measurement type")
        Assert.IsFalse(univMeasTPIde.Item(4).MeasurementTypes.MeasurementTypes.Contains(measTypeForA), _
                       "UniverseMeasurement for 'd' should contain 'd' measurement type, but not 'a' measurement types")
        Assert.IsFalse(univMeasTPIde.Item(4).MeasurementTypes.MeasurementTypes.Contains(measTypeForA2), _
                       "UniverseMeasurement for 'd' should contain 'd' measurement type, but not 'a' measurement types")
        Assert.IsFalse(univMeasTPIde.Item(4).MeasurementTypes.MeasurementTypes.Contains(measTypeB), _
                       "UniverseMeasurement for 'd' should contain 'd' measurement type, but not 'b' measurement types")
        Assert.IsFalse(univMeasTPIde.Item(4).MeasurementTypes.MeasurementTypes.Contains(measTypeC), _
                       "UniverseMeasurement for 'd' should contain 'd' measurement type, but not 'c' measurement types")
    End Sub
End Class
