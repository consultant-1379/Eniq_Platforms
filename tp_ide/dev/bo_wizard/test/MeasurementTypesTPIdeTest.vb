Imports NUnit.Framework
Imports NMock2
Imports System.Data.Common

<TestFixture()> _
Public Class MeasurementTypesTPIdeTest

    ' Test instance:
    Private testInstance As MeasurementTypesTPIde
    ' Set up mockery:
    Private mocks As NMock2.Mockery

    <SetUp()> _
    Public Sub SetUp()
        mocks = New NMock2.Mockery()
        testInstance = New MeasurementTypesTPIde()
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
    Public Sub getVectorSupportTestValidValue()
        Dim vectorSupportResult As Boolean = testInstance.getVectorSupport("1")
        Assert.IsTrue(vectorSupportResult, "VectorSupport value should be true for column value of '1'")
    End Sub

    <Test()> _
    Public Sub getVectorSupportTestEmptyString()
        Dim vectorSupportResult As Boolean = testInstance.getVectorSupport("")
        Assert.IsFalse(vectorSupportResult, "VectorSupport value should be false for column value of ''")
    End Sub

    <Test()> _
    Public Sub getVectorSupportTestZeroValue()
        Dim vectorSupportResult As Boolean = testInstance.getVectorSupport("0")
        Assert.IsFalse(vectorSupportResult, "VectorSupport value should be false for column value of '0'")
    End Sub

    <Test()> _
    Public Sub getVectorSupportTestNullValue()
        Dim vectorSupportResult As Boolean = testInstance.getVectorSupport(Nothing)
        Assert.IsFalse(vectorSupportResult, "VectorSupport value should be false for null column value")
    End Sub
End Class
