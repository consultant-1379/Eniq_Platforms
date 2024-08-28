Imports NUnit.Framework
Imports NMock2
Imports Designer
Imports System.Collections
Imports busobj

Public Class BOLatestConditionsTPIdeTest

    Private boLatestConditionsTPIde As BOLatestConditionsTPIde
    Private mocks As NMock2.Mockery

    <SetUp()> _
    Public Sub SetUp()
        boLatestConditionsTPIde = New BOLatestConditionsTPIde()
        mocks = New NMock2.Mockery()
    End Sub

    <TearDown()> _
    Public Sub TearDown()
        Try
            mocks.VerifyAllExpectationsHaveBeenMet()
        Catch ex As Exception
            Console.WriteLine(ex.ToString())
        End Try
        boLatestConditionsTPIde = Nothing
    End Sub

    <Test()> _
    Public Sub addLatestNHoursOrDaysConditionTest()
        ' Create mocks:
        Dim mockUniverseProxy As IUniverseProxy = mocks.NewMock(Of IUniverseProxy)()
        Dim mockClass As Designer.IClass = mocks.NewMock(Of Designer.IClass)()
        Dim mockCondition As Designer.PredefinedCondition = mocks.NewMock(Of Designer.PredefinedCondition)()
        Dim field As String = "DATETIME_ID"

        ' Set up a measurement type:
        Dim measType As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measType.ExtendedUniverse = "a"
        measType.RankTable = False
        measType.TypeName = "DC_E_SGSN_SGSN"
        measType.MeasurementTypeID = "DC_E_SGSN_SGSN"

        Dim boConditions As BOConditionsTPIde = New BOConditionsTPIde(mockUniverseProxy)

        expectAddLatestNHoursOrDaysCondition(mockUniverseProxy, mockCondition, mockClass)
        Dim tableType As String = "RAW"
        Dim result As Boolean = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(mockUniverseProxy, mockClass, tableType, _
                                                                                       field, boConditions, measType)
        Assert.IsTrue(result, "addLatestNHoursOrDaysCondition() should return true if RAW condition is added successfully")

        expectAddLatestNHoursOrDaysCondition(mockUniverseProxy, mockCondition, mockClass)
        tableType = "COUNT"
        result = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(mockUniverseProxy, mockClass, tableType, _
                                                                        field, boConditions, measType)
        Assert.IsTrue(result, "addLatestNHoursOrDaysCondition() should return true if COUNT condition is added successfully")

        expectAddLatestNHoursOrDaysCondition(mockUniverseProxy, mockCondition, mockClass)
        tableType = "DAY"
        result = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(mockUniverseProxy, mockClass, tableType, _
                                                                                       field, boConditions, measType)
        Assert.IsTrue(result, "addLatestNHoursOrDaysCondition() should return true if DAY condition is added successfully")

        expectAddLatestNHoursOrDaysCondition(mockUniverseProxy, mockCondition, mockClass)
        tableType = "DAYBH"
        result = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(mockUniverseProxy, mockClass, tableType, _
                                                                                       field, boConditions, measType)
        Assert.IsTrue(result, "addLatestNHoursOrDaysCondition() should return true if DAYBH condition is added successfully")
    End Sub

    <Test()> _
    Public Sub addLatestNHoursOrDaysConditionUnsupportedTableTest()
        ' Create mocks:
        Dim mockUniverseProxy As IUniverseProxy = mocks.NewMock(Of IUniverseProxy)()
        Dim mockClass As Designer.IClass = mocks.NewMock(Of Designer.IClass)()
        Dim mockCondition As Designer.PredefinedCondition = mocks.NewMock(Of Designer.PredefinedCondition)()
        Dim field As String = "DATETIME_ID"

        ' Set up a measurement type:
        Dim measType As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measType.ExtendedUniverse = "a"
        measType.RankTable = False
        measType.TypeName = "DC_E_SGSN_SGSN"
        measType.MeasurementTypeID = "DC_E_SGSN_SGSN"

        Dim boConditions As BOConditionsTPIde = New BOConditionsTPIde(mockUniverseProxy)
        Dim tableType As String = "RANKBH"

        ' There are no expectations here because getting the time interval will fail straight away:
        Dim result As Boolean = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(mockUniverseProxy, mockClass, tableType, _
                                                                                       field, boConditions, measType)
        Assert.IsFalse(result, "addLatestNHoursOrDaysCondition() should return false for RANKBH table")
    End Sub

    <Test()> _
    Public Sub addLatestNHoursOrDaysConditionAddFailsTest()
        ' Create mocks:
        Dim mockUniverseProxy As IUniverseProxy = mocks.NewMock(Of IUniverseProxy)()
        Dim mockClass As Designer.IClass = mocks.NewMock(Of Designer.IClass)()
        Dim mockCondition As Designer.PredefinedCondition = mocks.NewMock(Of Designer.PredefinedCondition)()
        Dim field As String = "DATETIME_ID"

        ' Set up a measurement type:
        Dim measType As MeasurementTypesTPIde.MeasurementType = New MeasurementTypesTPIde.MeasurementType
        measType.ExtendedUniverse = "a"
        measType.RankTable = False
        measType.TypeName = "DC_E_SGSN_SGSN"
        measType.MeasurementTypeID = "DC_E_SGSN_SGSN"

        Dim boConditions As BOConditionsTPIde = New BOConditionsTPIde(mockUniverseProxy)
        Dim tableType As String = "COUNT"

        ' Expect the situation where adding the condition fails completely:
        Expect.Once.On(mockUniverseProxy).Method("getPredefinedCondition").WithAnyArguments().Will(NMock2.Throw.Exception(New System.Exception("Error getting condition")))
        Expect.Once.On(mockUniverseProxy).Method("addPredefinedCondition").WithAnyArguments().Will(NMock2.Throw.Exception(New System.Exception("Error getting condition")))

        Dim result As Boolean = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(mockUniverseProxy, mockClass, tableType, _
                                                                                       field, boConditions, measType)
        Assert.IsFalse(result, "addLatestNHoursOrDaysCondition() should return false if adding the condition fails")
    End Sub

    ''
    'Sets up expectations for a call to addLatestNHoursOrDaysCondition() function.
    '@param mockUniverseProxy
    '@param mockCondition
    '@param mockClass
    Private Sub expectAddLatestNHoursOrDaysCondition(ByVal mockUniverseProxy As IUniverseProxy, ByVal mockCondition As Designer.PredefinedCondition, _
                                                     ByVal mockClass As Designer.IClass)


        Expect.Once.On(mockUniverseProxy).Method("getPredefinedCondition").WithAnyArguments().Will(NMock2.Throw.Exception(New System.Exception("Error getting condition")))
        Expect.Once.On(mockUniverseProxy).Method("addPredefinedCondition").WithAnyArguments().Will(NMock2.Return.Value(mockCondition))

        Expect.Exactly(2).On(mockClass).GetProperty("Name").Will(NMock2.Return.Value("Test_class_name"))
        Expect.Exactly(2).On(mockCondition).GetProperty("Name").Will(NMock2.Return.Value("Test_condition_name"))
        Expect.Once.On(mockCondition).SetProperty("Description")
        Expect.Once.On(mockCondition).SetProperty("Where")
    End Sub

End Class
