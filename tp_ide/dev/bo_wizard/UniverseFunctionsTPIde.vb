Option Strict Off
Option Explicit On

Imports System.Collections
Imports System.Reflection.MethodBase

Public Class UniverseFunctionsTPIde 'require variables to be declared before being used

    Dim Conxt As Designer.Context
    Dim Jn As Designer.Join

    Dim RowNum As Integer
    Dim RowNum2 As Integer

    Dim DefaultKeyMaxAmount As String
    Dim DefaultCounterMaxAmount As String

    Dim EniqEnv As String
    Dim TechPackTPIde As String
    Dim BaseTechPackTPIde As String

    Dim TechPackName As String
    Dim VendorRelease As String
    Dim ProductNumber As String
    Dim TechPackType As String
    Dim TPVersion As String
    Dim TPReleaseVersion As String
    Dim TPDescription As String
    Dim ObjectBHSupport As Boolean
    Dim ElementBHSupport As Boolean
    Dim FullAware As Boolean

    Dim UniverseName As String
    Dim UniverseDescription As String
    Dim UniverseFileName As String
    Dim UniverseExtension As String

    Dim cust_univ_name As String
    Dim cust_univ_file_name As String

    Dim CountersPerVerificationReport As Integer

    Dim MeasurementTypes_RowCount As Short

    Dim original_mts As MeasurementTypesTPIde
    Dim mt As MeasurementTypesTPIde.MeasurementType

    Dim UnvMts As UniverseMeasurementsTPIde
    Dim UnvMt As UniverseMeasurementsTPIde.UniverseMeasurement

    Dim all_cnts As CountersTPIde
    Dim cnts As CountersTPIde
    Dim cnt As New CountersTPIde.Counter

    Dim all_cnt_keys As CounterKeysTPIde
    Dim cnt_keys As CounterKeysTPIde
    Dim cnt_key As CounterKeysTPIde.CounterKey

    Dim univ_join As UnivJoinsTPIde.UnivJoin
    Dim extra_joins As UnivJoinsTPIde

    Dim univ_clss As UnivClassesTPIde
    Dim univ_cls As UnivClassesTPIde.UnivClass

    Dim RowCount As Integer
    Dim RowCount2 As Integer
    Dim mt_count As Integer
    Dim cnt_count As Long
    Dim cnt_key_count As Long

    Dim univ_cls_count As Long

    Dim rt As ReferenceTypesTPIde.ReferenceType
    Dim rd As ReferenceDatasTPIde.ReferenceData
    Dim rt_count As Long

    Dim dbCommand As System.Data.Odbc.OdbcCommand
    Dim dbReader As System.Data.Odbc.OdbcDataReader

    Dim tpAdoConn As String
    Dim baseAdoConn As String

    Dim tpConn As System.Data.Odbc.OdbcConnection
    Dim baseConn As System.Data.Odbc.OdbcConnection

    Dim bo_objects As IBOObjectsTPIde
    Dim bo_computedobjects As BOComputedObjectTPIde
    Dim bo_conditions As BOConditionsTPIde

    Dim pub_keys As PublicKeysTPIde
    Dim pub_key As PublicKeysTPIde.PublicKey

    Dim counterParse As Boolean
    Dim joinParse As Boolean
    Dim referenceParse As Boolean
    Dim objectParse As Boolean
    Dim additionalObjectParse As Boolean
    Dim conditionParse As Boolean
    Dim additionalConditionParse As Boolean
    Dim integrityParse As Boolean
    Dim extendedCountObject As Boolean
    Dim rankBusyHourFunctionality As Boolean

    Dim repobjs As ReportObjectsTPIde
    Dim repobj As ReportObjectsTPIde.ReportObject

    Dim repconds As ReportConditionsTPIde
    Dim repcond As ReportConditionsTPIde.ReportCondition

    Public Shared updatedTables As String
    Public Shared updatedClasses As String
    Public Shared updatedObjects As String
    Public Shared updatedConditions As String
    Public Shared updatedJoins As String
    Public Shared updatedContexts As String

    ' Universe:
    Dim universeForReports As Designer.Universe
    Dim hiddenObjects As ArrayList = New ArrayList

    ' Logging variables:
    Dim className As String = "UniverseFunctionsTPIde.vb"

    ' List of class key objects to exclude    
    Protected excludedKeyObjects As String()
    Protected excludedKeyObjectsForRankBH As String()

    Dim totalReportsCreated As Integer

    Dim DesignerApp As Designer.IApplication
    ' Dim BoApp As busobj.Application
    Dim tpUtilities As ITPUtilitiesTPIde
    Dim databaseProxy As DBProxy
    Dim universeProxy As IUniverseProxy

    ' To be used only for test:
    Public Sub New(ByVal databaseProxy As DBProxy, ByVal tpUtilities As ITPUtilitiesTPIde)
        Me.databaseProxy = databaseProxy
        Me.tpUtilities = tpUtilities
    End Sub

    Public Sub New()
        tpUtilities = createTPUtilities()
        databaseProxy = New DatabaseProxy()
    End Sub

    Public Overridable Function createTPUtilities() As ITPUtilitiesTPIde
        Return New TPUtilitiesTPIde
    End Function

    Private Function Universe_AddCounters(ByRef Univ As Object, ByRef CMTechPack As Boolean, ByRef EBSTechPack As Boolean, ByRef mts As MeasurementTypesTPIde, _
                                          ByRef vector_rds As ReferenceDatasTPIde) As Boolean
        'Optimized version
        Dim parentCountersClass As Designer.Class  ' x Counters class
        Dim mtCountersClass As Designer.Class ' x Counters, mt.TypeName class
        Dim mt_RawCountersClass As Designer.Class ' x Counters, mt.TypeName class + _RAW class
        Dim Obj As Designer.Object
        'Dim Obj2 As Designer.Object
        'Dim Tbl As Designer.Table
        Dim ClassTree() As String
        Dim TreeCount As Integer
        Dim HierarchyRootClass As String
        Dim UsedClass As String
        Dim count As Integer
        Dim vectorTable As String
        Dim aggr_awareFormula As String
        Dim tpUtilities As New TPUtilitiesTPIde


        Dim universeProxy As IUniverseProxy = New UniverseProxy(Univ)
        Dim bo_objects As New BOObjectsTPIde(universeProxy)
        Dim univ_classes As New UnivClassesTPIde

        Dim ObjNum As Designer.Object
        'modified /added for TR HK 80515
        Console.WriteLine("Adds Counters to the Universe")
        For mt_count = 1 To mts.Count
            Try
                mt = mts.Item(mt_count)
                Trace.WriteLine("Adding counter for Measurement: " & mt.MeasurementTypeID)
                Trace.WriteLine("it has " & mt.Counters.Count & " counters")
                If mt.MeasurementTypeID <> "" AndAlso mt.RankTable = False Then
                    If CMTechPack = True Then
                        parentCountersClass = univ_classes.addClass(Univ, Univ.Classes.FindClass("Parameters"), mt.MeasurementTypeClassDescription & " Parameters", mt.MeasurementTypeClassDescription & " Parameters")
                    Else
                        parentCountersClass = univ_classes.addClass(Univ, Univ.Classes.FindClass("Counters"), mt.MeasurementTypeClassDescription & " Counters", mt.MeasurementTypeClassDescription & " Counters")
                    End If
                    If mt.RankTable = False AndAlso mt.CreateCountTable = True Then 'COUNT tables
                        mtCountersClass = univ_classes.addClass(Univ, parentCountersClass, mt.TypeName, mt.Description)
                        mt_RawCountersClass = univ_classes.addClass(Univ, parentCountersClass, mt.TypeName & "_RAW", mt.Description)

                        cnts = mt.Counters
                        For cnt_count = 1 To cnts.Count
                            cnt = cnts.Item(cnt_count)
                            Trace.WriteLine("COUNT Table Counter: " & cnt.CounterName & " It's Counter Type: " & cnt.CounterType)
                            mtCountersClass = parentCountersClass.Classes.FindClass(mt.TypeName)
                            mt_RawCountersClass = parentCountersClass.Classes.FindClass(mt.TypeName & "_RAW")

                            If cnt.UnivObject <> "" Then
                                UsedClass = ""
                                HierarchyRootClass = mt.TypeName
                                If cnt.UnivClass <> "" Then
                                    UsedClass = cnt.UnivClass
                                    ClassTree = Split(cnt.UnivClass, "//")
                                    For TreeCount = 0 To UBound(ClassTree)

                                        mtCountersClass = univ_classes.addClass(Univ, parentCountersClass.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                        HierarchyRootClass = ClassTree(TreeCount)
                                        UsedClass = ClassTree(TreeCount)
                                    Next TreeCount
                                End If

                                If UsedClass <> "" Then
                                    mt_RawCountersClass = univ_classes.addClass(Univ, parentCountersClass.Classes.FindClass(mt.TypeName & "_RAW"), UsedClass & "_RAW", UsedClass & "_RAW")
                                End If

                                If cnt.oneAggrFormula = True AndAlso cnt.oneAggrValue <> "NONE" AndAlso cnt.oneAggrValue <> "" Then 'If Time Aggregation and Group Aggregation are same and Aggregation is (SUM,MIN,MAX,AVG)
                                    If cnt.oneAggrValue <> "SUM" And cnt.oneAggrValue <> "" Then
                                        Obj = bo_objects.addObject(mtCountersClass, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue)
                                    Else
                                        Obj = bo_objects.addObject(mtCountersClass, cnt, True, cnt.oneAggrValue)
                                    End If
                                    'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                        If mt.DayAggregation = True Then
                                            'placeholder for extended COUNT functionality
                                            If extendedCountObject = True Then
                                                aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName + ")," & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName + ")," & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName + ")," & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.TypeName & "_DELTA." & cnt.CounterName + "))"
                                                'placeholder for extended COUNT functionality
                                            Else
                                                aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName + ")," & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName + ")," & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName + "))"
                                            End If
                                        Else
                                            'placeholder for extended COUNT functionality
                                            If extendedCountObject = True Then
                                                aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName + ")," & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName + ")," & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.TypeName & "_DELTA." & cnt.CounterName + "))"
                                                'placeholder for extended COUNT functionality
                                            Else
                                                aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName + ")," & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName + "))"
                                            End If
                                        End If
                                    Else
                                        If mt.DayAggregation = True Then
                                            'placeholder for extended COUNT functionality
                                            If extendedCountObject = True Then
                                                aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName + ")," & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName + ")," & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.TypeName & "_DELTA." & cnt.CounterName + "))"
                                                'placeholder for extended COUNT functionality
                                            Else
                                                aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName + ")," & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName + "))"
                                            End If
                                        Else
                                            'placeholder for extended COUNT functionality
                                            If extendedCountObject = True Then
                                                aggr_awareFormula = "@aggregate_aware(" & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName + ")," & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.TypeName & "_DELTA." & cnt.CounterName + "))"
                                                'placeholder for extended COUNT functionality
                                            Else
                                                aggr_awareFormula = (LCase(cnt.oneAggrValue)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName + ")"
                                            End If
                                        End If
                                    End If
                                    If parseCounterObject(bo_objects, Obj, mtCountersClass, CMTechPack, aggr_awareFormula, cnt) = False Then
                                        Return False
                                    End If


                                    ' If there is no aggregation value set, put the counter in the _RAW class:
                                    If cnt.oneAggrValue <> "SUM" And cnt.oneAggrValue <> "" Then
                                        Obj = bo_objects.addObject(mt_RawCountersClass, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue)
                                    Else
                                        Obj = bo_objects.addObject(mt_RawCountersClass, cnt, True, cnt.oneAggrValue)
                                    End If
                                    If parseCounterObject(bo_objects, Obj, mt_RawCountersClass, CMTechPack, LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & ")", cnt) = False Then
                                        Return False
                                    End If
                                ElseIf cnt.oneAggrFormula = True AndAlso (cnt.oneAggrValue = "NONE" OrElse cnt.oneAggrValue = "") Then  'If Time Aggregation and Group Aggregation are same and Aggregation is None or empty
                                    Obj = bo_objects.addObject(mtCountersClass, cnt, cnt.UnivObject & " (none)", True, cnt.oneAggrValue)
                                    'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                        If mt.DayAggregation = True Then
                                            'placeholder for extended COUNT functionality
                                            If extendedCountObject = True Then
                                                aggr_awareFormula = "@aggregate_aware(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ",DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ",DC." + cnt.TypeName & "_COUNT." & cnt.CounterName + ",DC." + cnt.TypeName & "_DELTA." & cnt.CounterName + ")"
                                                'placeholder for extended COUNT functionality
                                            Else
                                                aggr_awareFormula = "@aggregate_aware(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ",DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ",DC." + cnt.TypeName & "_COUNT." & cnt.CounterName + ")"
                                            End If
                                        Else
                                            'placeholder for extended COUNT functionality
                                            If extendedCountObject = True Then
                                                aggr_awareFormula = "@aggregate_aware(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ",DC." + cnt.TypeName & "_COUNT." & cnt.CounterName + ",DC." + cnt.TypeName & "_DELTA." & cnt.CounterName + ")"
                                                'placeholder for extended COUNT functionality
                                            Else
                                                aggr_awareFormula = "@aggregate_aware(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ",DC." + cnt.TypeName & "_COUNT." & cnt.CounterName + ")"
                                            End If
                                        End If

                                    Else
                                        If mt.DayAggregation = True Then
                                            'placeholder for extended COUNT functionality
                                            If extendedCountObject = True Then
                                                aggr_awareFormula = "@aggregate_aware(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ",DC." + cnt.TypeName & "_COUNT." & cnt.CounterName + ",DC." + cnt.TypeName & "_DELTA." & cnt.CounterName + ")"
                                                'placeholder for extended COUNT functionality
                                            Else
                                                aggr_awareFormula = "@aggregate_aware(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ",DC." + cnt.TypeName & "_COUNT." & cnt.CounterName + ")"
                                            End If
                                        Else
                                            'placeholder for extended COUNT functionality
                                            If extendedCountObject = True Then
                                                aggr_awareFormula = "@aggregate_aware(DC." + cnt.TypeName & "_COUNT." & cnt.CounterName + ",DC." + cnt.TypeName & "_DELTA." & cnt.CounterName + ")"
                                                'placeholder for extended COUNT functionality
                                            Else
                                                aggr_awareFormula = "DC." + cnt.TypeName & "_COUNT." & cnt.CounterName + ""
                                            End If
                                        End If

                                    End If
                                    If parseCounterObject(bo_objects, Obj, mtCountersClass, CMTechPack, aggr_awareFormula, cnt) = False Then
                                        Return False
                                    End If

                                    Obj = bo_objects.addObject(mt_RawCountersClass, cnt, cnt.UnivObject & " (none)", True, cnt.oneAggrValue)
                                    If parseCounterObject(bo_objects, Obj, mt_RawCountersClass, CMTechPack, "DC." & cnt.TypeName & "_RAW." & cnt.CounterName, cnt) = False Then
                                        Return False
                                    End If
                                ElseIf cnt.oneAggrFormula = False Then 'If Time Aggregation and Group Aggregation are different
                                    For count = 0 To UBound(cnt.Aggregations)
                                        If cnt.Aggregations(count) <> "" Then
                                            Obj = bo_objects.addObject(mtCountersClass, cnt, cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")", True, cnt.Aggregations(count))
                                        Else
                                            Obj = bo_objects.addObject(mtCountersClass, cnt, cnt.UnivObject, True, cnt.Aggregations(count))
                                        End If
                                        'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                                        If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                            If mt.DayAggregation = True Then
                                                'placeholder for extended COUNT functionality
                                                If extendedCountObject = True Then
                                                    aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DELTA." & cnt.CounterName & "))"
                                                    'placeholder for extended COUNT functionality
                                                Else
                                                    aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName & "))"
                                                End If
                                            Else
                                                'placeholder for extended COUNT functionality
                                                If extendedCountObject = True Then
                                                    aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DELTA." & cnt.CounterName & "))"
                                                    'placeholder for extended COUNT functionality
                                                Else
                                                    aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName & "))"
                                                End If
                                            End If

                                        Else
                                            If mt.DayAggregation = True Then
                                                'placeholder for extended COUNT functionality
                                                If extendedCountObject = True Then
                                                    aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DELTA." & cnt.CounterName & "))"
                                                    'placeholder for extended COUNT functionality
                                                Else
                                                    aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName & "))"
                                                End If
                                            Else
                                                'placeholder for extended COUNT functionality
                                                If extendedCountObject = True Then
                                                    aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DELTA." & cnt.CounterName & "))"
                                                    'placeholder for extended COUNT functionality
                                                Else
                                                    aggr_awareFormula = LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName & ")"
                                                End If
                                            End If

                                        End If
                                        If parseCounterObject(bo_objects, Obj, mtCountersClass, CMTechPack, aggr_awareFormula, cnt) = False Then
                                            Return False
                                        End If



                                        If cnt.Aggregations(count) <> "" Then
                                            Obj = bo_objects.addObject(mt_RawCountersClass, cnt, cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")", True, cnt.Aggregations(count))
                                        Else
                                            Obj = bo_objects.addObject(mt_RawCountersClass, cnt, cnt.UnivObject, True, cnt.Aggregations(count))
                                        End If
                                        If parseCounterObject(bo_objects, Obj, mt_RawCountersClass, CMTechPack, LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & ")", cnt) = False Then
                                            Return False
                                        End If
                                    Next count
                                Else
                                    Trace.WriteLine("Universe Object '" & cnt.UnivObject & "' for Counter '" & cnt.CounterName & "' in Fact Table '" & cnt.TypeName & "' not added/modified")
                                End If
                            Else

                            End If

                            'Vector objects here
                            If cnt.CounterType = "VECTOR" AndAlso mt.VectorSupport = True Then

                                'Check for whether vector counter has range or not.
                                If (tpUtilities.isVectorRangePresent(cnt.MeasurementTypeID, cnt.CounterName, tpConn)) Then
                                    vectorTable = Replace(cnt.MeasurementTypeID, "DC_", "DIM_", , 1) & "_" & cnt.CounterName
                                For count = 1 To vector_rds.Count
                                    rd = vector_rds.Item(count)
                                    If vectorTable = rd.ReferenceTypeID AndAlso rd.UnivObject <> "" Then
                                        mtCountersClass = parentCountersClass.Classes.FindClass(mt.TypeName)
                                        mt_RawCountersClass = parentCountersClass.Classes.FindClass(mt.TypeName & "_RAW")

                                        UsedClass = ""
                                        HierarchyRootClass = mt.TypeName
                                        If cnt.UnivClass <> "" Then
                                            UsedClass = cnt.UnivClass
                                            ClassTree = Split(cnt.UnivClass, "//")
                                            For TreeCount = 0 To UBound(ClassTree)
                                                mtCountersClass = univ_classes.addClass(Univ, parentCountersClass.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                                HierarchyRootClass = ClassTree(TreeCount)
                                                UsedClass = ClassTree(TreeCount)
                                            Next TreeCount
                                        End If

                                        If UsedClass <> "" Then
                                            mt_RawCountersClass = univ_classes.addClass(Univ, parentCountersClass.Classes.FindClass(mt.TypeName & "_RAW"), UsedClass & "_RAW", UsedClass & "_RAW")
                                        End If

                                        Obj = bo_objects.addObject(mtCountersClass, rd, False)
                                        If parseReferenceObject(bo_objects, Obj, mtCountersClass, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID) = False Then
                                            Return False
                                        End If
                                        Obj = bo_objects.addObject(mt_RawCountersClass, rd, False)
                                        If parseReferenceObject(bo_objects, Obj, mt_RawCountersClass, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID) = False Then
                                            Return False
                                        End If
                                        'parseCounterObject(bo_objects, Obj, Cls3)
                                    End If
                                Next count
                                Else
                                    ' Nothing
                                    Trace.WriteLine("No range is defined for the type id " & cnt.MeasurementTypeID & " Counter: " & cnt.CounterName & ", not adding this vector counter")
                                    Console.WriteLine("No range is defined for the type id " & cnt.MeasurementTypeID & " Counter: " & cnt.CounterName & ", not adding this vector counter")
                                End If
                                'Check for whether vector counter has range or not.
                            End If
                            'Vector
                        Next cnt_count

                    End If
                    If mt.RankTable = False AndAlso mt.CreateCountTable = False Then 'Regular tables
                        mtCountersClass = univ_classes.addClass(Univ, parentCountersClass, mt.TypeName, mt.Description)

                        cnts = mt.Counters
                        For cnt_count = 1 To cnts.Count
                            cnt = cnts.Item(cnt_count)
                            Trace.WriteLine("Regular Table Counter: " & cnt.CounterName & " It's Counter Type: " & cnt.CounterType)
                            mtCountersClass = parentCountersClass.Classes.FindClass(mt.TypeName)

                            If cnt.UnivObject <> "" Then

                                HierarchyRootClass = mt.TypeName
                                If cnt.UnivClass <> "" Then
                                    UsedClass = cnt.UnivClass
                                    ClassTree = Split(cnt.UnivClass, "//")
                                    For TreeCount = 0 To UBound(ClassTree)
                                        mtCountersClass = univ_classes.addClass(Univ, parentCountersClass.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                        HierarchyRootClass = ClassTree(TreeCount)
                                        UsedClass = ClassTree(TreeCount)
                                    Next TreeCount
                                End If

                                If cnt.oneAggrFormula = True AndAlso cnt.oneAggrValue <> "NONE" AndAlso cnt.oneAggrValue <> "" Then 'If Time Aggregation and Group Aggregation are same and Aggregation is (SUM,MIN,MAX,AVG)
                                    If cnt.oneAggrValue <> "SUM" And cnt.oneAggrValue <> "" Then
                                        Obj = bo_objects.addObject(mtCountersClass, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue)
                                    Else
                                        Obj = bo_objects.addObject(mtCountersClass, cnt, True, cnt.oneAggrValue)
                                    End If
                                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                        If mt.DayAggregation = True Then
                                            aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ")," & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & "))"
                                        Else
                                            aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & "))"
                                        End If
                                    Else
                                        If mt.DayAggregation = True Then
                                            aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ")," & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & "))"
                                        Else
                                            aggr_awareFormula = LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & ")"
                                        End If
                                    End If
                                    If parseCounterObject(bo_objects, Obj, mtCountersClass, CMTechPack, aggr_awareFormula, cnt) = False Then
                                        Return False
                                    End If

                                ElseIf cnt.oneAggrFormula = True AndAlso (cnt.oneAggrValue = "NONE" OrElse cnt.oneAggrValue = "NONE") Then   'If Time Aggregation and Group Aggregation are same and Aggregation is None
                                    Obj = bo_objects.addObject(mtCountersClass, cnt, cnt.UnivObject & " (none)", True, cnt.oneAggrValue)
                                    If mt.PlainTable = True Then
                                        If parseCounterObject(bo_objects, Obj, mtCountersClass, CMTechPack, "DC." & cnt.TypeName & "." & cnt.CounterName, cnt) = False Then
                                            Return False
                                        End If
                                    ElseIf CMTechPack = True Then
                                        If parseCounterObject(bo_objects, Obj, mtCountersClass, CMTechPack, "DC." & cnt.TypeName & "_RAW." & cnt.CounterName, cnt) = False Then
                                            Return False
                                        End If
                                    Else
                                        'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                                        If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                            If mt.DayAggregation = True Then
                                                aggr_awareFormula = "@aggregate_aware(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ",DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ",DC." & cnt.TypeName & "_RAW." & cnt.CounterName & ")"
                                            Else
                                                aggr_awareFormula = "@aggregate_aware(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ",DC." & cnt.TypeName & "_RAW." & cnt.CounterName & ")"
                                            End If

                                        Else
                                            If mt.DayAggregation = True Then
                                                aggr_awareFormula = "@aggregate_aware(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ",DC." & cnt.TypeName & "_RAW." & cnt.CounterName & ")"
                                            Else
                                                aggr_awareFormula = "DC." & cnt.TypeName & "_RAW." & cnt.CounterName & ""
                                            End If
                                        End If
                                        If parseCounterObject(bo_objects, Obj, mtCountersClass, CMTechPack, aggr_awareFormula, cnt) = False Then
                                            Return False
                                        End If
                                    End If
                                ElseIf cnt.oneAggrFormula = False Then 'If Time Aggregation and Group Aggregation are different
                                    For count = 0 To UBound(cnt.Aggregations)
                                        If cnt.Aggregations(count) <> "" Then
                                            Obj = bo_objects.addObject(mtCountersClass, cnt, cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")", True, cnt.Aggregations(count))
                                        Else
                                            Obj = bo_objects.addObject(mtCountersClass, cnt, cnt.UnivObject, True, cnt.Aggregations(count))
                                        End If
                                        'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                                        If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                            If mt.DayAggregation = True Then
                                                aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & "))"
                                            Else
                                                aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & "))"
                                            End If

                                        Else
                                            If mt.DayAggregation = True Then
                                                aggr_awareFormula = "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & "))"
                                            Else
                                                aggr_awareFormula = LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & ")"
                                            End If

                                        End If
                                        If parseCounterObject(bo_objects, Obj, mtCountersClass, CMTechPack, aggr_awareFormula, cnt) = False Then
                                            Return False
                                        End If
                                    Next count
                                Else
                                    Trace.WriteLine("Universe Object '" & cnt.UnivObject & "' for Counter '" & cnt.CounterName & "' in Fact Table '" & cnt.TypeName & "' not added/modified")
                                End If
                            Else
                            End If

                            'Vector objects here
                            If cnt.CounterType = "VECTOR" AndAlso mt.VectorSupport = True Then
                                'Check for whether vector counter has range or not.
                                If (tpUtilities.isVectorRangePresent(cnt.MeasurementTypeID, cnt.CounterName, tpConn)) Then
                                    vectorTable = Replace(cnt.TypeName, "DC_", "DIM_", , 1) & "_" & cnt.CounterName
                                For count = 1 To vector_rds.Count
                                    rd = vector_rds.Item(count)
                                    If vectorTable = rd.ReferenceTypeID AndAlso rd.UnivObject <> "" Then
                                        mtCountersClass = parentCountersClass.Classes.FindClass(mt.TypeName)

                                        HierarchyRootClass = mt.TypeName
                                        If cnt.UnivClass <> "" Then
                                            UsedClass = cnt.UnivClass
                                            ClassTree = Split(cnt.UnivClass, "//")
                                            For TreeCount = 0 To UBound(ClassTree)
                                                mtCountersClass = univ_classes.addClass(Univ, parentCountersClass.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                                HierarchyRootClass = ClassTree(TreeCount)
                                                UsedClass = ClassTree(TreeCount)
                                            Next TreeCount
                                        End If
                                        Obj = bo_objects.addObject(mtCountersClass, rd, False)
                                        If parseReferenceObject(bo_objects, Obj, mtCountersClass, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID) = False Then
                                            Return False
                                        End If
                                    End If
                                Next count
                                Else
                                    'Nothing
                                    Trace.WriteLine("No range is defined for the type id " & cnt.MeasurementTypeID & " Counter: " & cnt.CounterName & ", not adding this vector counter")
                                    Console.WriteLine("No range is defined for the type id " & cnt.MeasurementTypeID & " Counter: " & cnt.CounterName & ", not adding this vector counter")
                                End If
                                'Check for whether vector counter has range or not.
                            End If
                            'Vector
                        Next cnt_count

                    End If

                    removeObjectsForEBS(EBSTechPack, mtCountersClass, mt.TypeName, cnts, universeProxy, bo_objects)

                End If
            Catch ex As Exception
                Trace.WriteLine("Error adding counters for measurement type: " & mt.TypeName & ". Error: " & ex.ToString())
            End Try
        Next mt_count
        Return True

    End Function

    ''
    ''Removes objects from a universe class for an EBS tech pack.
    '@param EBSTechPack         
    '@param mtCountersClass
    '@param measTypeName
    '@param cnts
    '@param universeProxy
    Public Sub removeObjectsForEBS(ByVal EBSTechPack As Boolean, ByVal mtCountersClass As Designer.Class, _
                                 ByVal measTypeName As String, ByVal cnts As CountersTPIde, ByVal universeProxy As IUniverseProxy, _
                                 ByVal bo_objects As IBOObjectsTPIde)
        Trace.WriteLine("UniverseFunctionsTPIde, removeObjectsForEBS(): Removing objects from universe for : " & measTypeName)
        If (EBSTechPack = True) Then
            Try
                If (mtCountersClass Is Nothing) Then
                    mtCountersClass = universeProxy.getClass(measTypeName)
                    If (mtCountersClass Is Nothing) Then
                        Throw New Exception("UniverseFunctionsTPIde, removeObjectsForEBS(): couldn't find universe class for " & measTypeName)
                    End If
                End If
                bo_objects.removeObjectsForEBS(mtCountersClass, cnts)
            Catch ex As Exception
                Trace.WriteLine("UniverseFunctionsTPIde, removeObjectsForEBS(): Error removing objects from universe class: " & ex.ToString())
            End Try
        End If
    End Sub                    

    ''
    ' Adds computed counter objects to universe.
    '
    ' @param Univ Specifies reference to universe
    ' @param CMTechPack Specifies tech pack type. Value is True if tech tech pack is CM. Value is False if tech tech pack is PM.
    Private Function Universe_AddComputedCounters(ByRef Univ As Object, ByRef CMTechPack As Boolean, ByRef EBSTechPack As Boolean, ByRef mts As MeasurementTypesTPIde, ByRef vector_rds As ReferenceDatasTPIde) As Boolean

        Dim Cls As Designer.Class
        Dim Cls2 As Designer.Class
        Dim Cls3 As Designer.Class
        Dim Obj As Designer.Object
        'Dim Obj2 As Designer.Object
        'Dim Tbl As Designer.Table
        Dim ClassTree() As String
        Dim TreeCount As Integer
        Dim HierarchyRootClass As String
        Dim UsedClass As String
        Dim count As Integer
        Dim vectorTable As String
        Dim tpUtilities As New TPUtilitiesTPIde

        Dim bo_computedobjects As New BOComputedObjectTPIde

        Dim univ_classes As New UnivClassesTPIde

        Dim ObjNum As Designer.Object

        For mt_count = 1 To mts.Count

            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" AndAlso mt.RankTable = False Then
                Trace.WriteLine("Adding computed counter for Measurement: " & mt.MeasurementTypeID)
                Trace.WriteLine("it has " & mt.Counters.Count & "computed counters")
                If CMTechPack = True Then
                    Cls = univ_classes.addClass(Univ, Univ.Classes.FindClass("Parameters"), mt.MeasurementTypeClassDescription & " Parameters", mt.MeasurementTypeClassDescription & " Parameters")
                Else
                    Cls = univ_classes.addClass(Univ, Univ.Classes.FindClass("Counters"), mt.MeasurementTypeClassDescription & " Counters", mt.MeasurementTypeClassDescription & " Counters")
                End If
                If mt.RankTable = False AndAlso mt.CreateCountTable = True Then 'COUNT tables
                    Cls2 = univ_classes.addClass(Univ, Cls, mt.TypeName, mt.Description)
                    Cls3 = univ_classes.addClass(Univ, Cls, mt.TypeName & "_RAW", mt.Description)

                    cnts = mt.Counters
                    For cnt_count = 1 To cnts.Count
                        cnt = cnts.Item(cnt_count)
                        Trace.WriteLine("COUNT Table computed Counter: " & cnt.CounterName & " It's Counter Type: " & cnt.CounterType)
                        Cls2 = Cls.Classes.FindClass(mt.TypeName)
                        Cls3 = Cls.Classes.FindClass(mt.TypeName & "_RAW")

                        If cnt.UnivObject <> "" Then
                            UsedClass = ""
                            HierarchyRootClass = mt.TypeName
                            If cnt.UnivClass <> "" Then
                                UsedClass = cnt.UnivClass
                                ClassTree = Split(cnt.UnivClass, "//")
                                For TreeCount = 0 To UBound(ClassTree)

                                    Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                    HierarchyRootClass = ClassTree(TreeCount)
                                    UsedClass = ClassTree(TreeCount)
                                Next TreeCount
                            End If

                            If UsedClass <> "" Then
                                Cls3 = univ_classes.addClass(Univ, Cls.Classes.FindClass(mt.TypeName & "_RAW"), UsedClass & "_RAW", UsedClass & "_RAW")
                            End If

                            If cnt.oneAggrFormula = True AndAlso cnt.oneAggrValue <> "NONE" AndAlso cnt.oneAggrValue <> "" Then 'If Time Aggregation and Group Aggregation are same and Aggregation is (SUM,MIN,MAX,AVG)
                                If cnt.oneAggrValue <> "SUM" Then
                                    Obj = bo_computedobjects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue)
                                Else
                                    Obj = bo_computedobjects.addObject(Cls2, cnt, True, cnt.oneAggrValue)
                                End If

                                'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                    If parseCounterComputedObject(bo_computedobjects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName + ")," & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName + ")," & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName + "))", cnt) = False Then 'JTS
                                        Return False
                                    End If

                                Else

                                    If parseCounterComputedObject(bo_computedobjects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName + ")," & (LCase(cnt.oneAggrValue)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName + "))", cnt) = False Then  'JTS
                                        Return False
                                    End If
                                End If

                                If cnt.oneAggrValue <> "SUM" Then
                                    Obj = bo_computedobjects.addObject(Cls3, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue)
                                Else
                                    Obj = bo_computedobjects.addObject(Cls3, cnt, True, cnt.oneAggrValue)

                                End If
                                If parseCounterComputedObject(bo_computedobjects, Obj, Cls3, CMTechPack, LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & ")", cnt) = False Then
                                    Return False
                                End If

                            ElseIf cnt.oneAggrFormula = True AndAlso cnt.oneAggrValue = "NONE" Then  'If Time Aggregation and Group Aggregation are same and Aggregation is None                                
                                Obj = bo_computedobjects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue)
                                'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT

                                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then

                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ",DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ",DC." + cnt.TypeName & "_COUNT." & cnt.CounterName + ")", cnt) = False Then
                                        Return False
                                    End If

                                    If parseCounterComputedObject(bo_computedobjects, Obj, Cls2, CMTechPack, "@aggregate_aware(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ",DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ",DC." + cnt.TypeName & "_COUNT." & cnt.CounterName + ")", cnt) = False Then
                                        Return False
                                    End If

                                Else
                                    If parseCounterObject(bo_objects, Obj, Cls2, CMTechPack, "@aggregate_aware(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ",DC." + cnt.TypeName & "_COUNT." & cnt.CounterName + ")", cnt) = False Then
                                        Return False
                                    End If

                                    If parseCounterComputedObject(bo_computedobjects, Obj, Cls2, CMTechPack, "@aggregate_aware(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ",DC." + cnt.TypeName & "_COUNT." & cnt.CounterName + ")", cnt) = False Then
                                        Return False
                                    End If
                                End If

                                Obj = bo_objects.addObject(Cls3, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue)

                                Obj = bo_computedobjects.addObject(Cls3, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue)

                                If parseCounterObject(bo_objects, Obj, Cls3, CMTechPack, "DC." & cnt.TypeName & "_RAW." & cnt.CounterName, cnt) = False Then
                                    Return False
                                End If

                                If parseCounterComputedObject(bo_computedobjects, Obj, Cls3, CMTechPack, "DC." & cnt.TypeName & "_RAW." & cnt.CounterName, cnt) = False Then
                                    Return False
                                End If


                            ElseIf cnt.oneAggrFormula = False Then 'If Time Aggregation and Group Aggregation are different
                                For count = 0 To UBound(cnt.Aggregations)
                                    Obj = bo_computedobjects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")", True, cnt.Aggregations(count))

                                    'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                        If parseCounterComputedObject(bo_computedobjects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName & "))", cnt) = False Then
                                            Return False
                                        End If

                                    Else
                                        If parseCounterComputedObject(bo_computedobjects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_COUNT." & cnt.CounterName & "))", cnt) = False Then
                                            Return False
                                        End If
                                    End If

                                    Obj = bo_computedobjects.addObject(Cls3, cnt, cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")", True, cnt.Aggregations(count))

                                    If parseCounterComputedObject(bo_computedobjects, Obj, Cls3, CMTechPack, LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & ")", cnt) = False Then
                                        Return False
                                    End If

                                Next count
                            Else
                                Trace.WriteLine("Universe Object '" & cnt.UnivObject & "' for Counter '" & cnt.CounterName & "' in Fact Table '" & cnt.TypeName & "' not added/modified")
                            End If
                        Else

                        End If

                        'Vector objects here
                        If cnt.CounterType = "VECTOR" AndAlso mt.VectorSupport = True Then
                            'Check for whether vector counter has range or not.
                            If (tpUtilities.isVectorRangePresent(cnt.MeasurementTypeID, cnt.CounterName, tpConn)) Then
                                vectorTable = Replace(cnt.MeasurementTypeID, "DC_", "DIM_", , 1) & "_" & cnt.CounterName
                            For count = 1 To vector_rds.Count
                                rd = vector_rds.Item(count)
                                If vectorTable = rd.ReferenceTypeID AndAlso rd.UnivObject <> "" Then
                                    Cls2 = Cls.Classes.FindClass(mt.TypeName)
                                    Cls3 = Cls.Classes.FindClass(mt.TypeName & "_RAW")

                                    UsedClass = ""
                                    HierarchyRootClass = mt.TypeName
                                    If cnt.UnivClass <> "" Then
                                        UsedClass = cnt.UnivClass
                                        ClassTree = Split(cnt.UnivClass, "//")
                                        For TreeCount = 0 To UBound(ClassTree)
                                            Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                            HierarchyRootClass = ClassTree(TreeCount)
                                            UsedClass = ClassTree(TreeCount)
                                        Next TreeCount
                                    End If

                                    If UsedClass <> "" Then
                                        Cls3 = univ_classes.addClass(Univ, Cls.Classes.FindClass(mt.TypeName & "_RAW"), UsedClass & "_RAW", UsedClass & "_RAW")
                                    End If

                                    Obj = bo_computedobjects.addObject(Cls2, rd, False)

                                    If parseReferenceComputedObject(bo_computedobjects, Obj, Cls2, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID) = False Then
                                        Return False
                                    End If

                                    Obj = bo_computedobjects.addObject(Cls3, rd, False)

                                    If parseReferenceComputedObject(bo_computedobjects, Obj, Cls3, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID) = False Then
                                        Return False
                                    End If
                                    'parseCounterObject(bo_objects, Obj, Cls3)
                                End If
                            Next count
                            Else
                                ' Nothing
                                Trace.WriteLine("No range is defined for the type id " & cnt.MeasurementTypeID & " Counter: " & cnt.CounterName & ", not adding this vector computed counter")
                                Console.WriteLine("No range is defined for the type id " & cnt.MeasurementTypeID & " Counter: " & cnt.CounterName & ", not adding this vector computed counter")
                            End If
                            'Check for whether vector counter has range or not.
                        End If
                        'Vector
                    Next cnt_count

                End If
                If mt.RankTable = False AndAlso mt.CreateCountTable = False Then 'Regular tables
                    Cls2 = univ_classes.addClass(Univ, Cls, mt.TypeName, mt.Description)

                    cnts = mt.Counters
                    For cnt_count = 1 To cnts.Count
                        cnt = cnts.Item(cnt_count)
                        Trace.WriteLine("REGULAR Table computed Counter: " & cnt.CounterName & " It's Counter Type: " & cnt.CounterType)
                        Cls2 = Cls.Classes.FindClass(mt.TypeName)

                        If cnt.UnivObject <> "" Then

                            HierarchyRootClass = mt.TypeName
                            If cnt.UnivClass <> "" Then
                                UsedClass = cnt.UnivClass
                                ClassTree = Split(cnt.UnivClass, "//")
                                For TreeCount = 0 To UBound(ClassTree)
                                    Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                    HierarchyRootClass = ClassTree(TreeCount)
                                    UsedClass = ClassTree(TreeCount)
                                Next TreeCount
                            End If

                            If cnt.oneAggrFormula = True AndAlso cnt.oneAggrValue <> "NONE" AndAlso cnt.oneAggrValue <> "" Then 'If Time Aggregation and Group Aggregation are same and Aggregation is (SUM,MIN,MAX,AVG)
                                If cnt.oneAggrValue <> "SUM" Then
                                    Obj = bo_computedobjects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue)
                                Else
                                    Obj = bo_computedobjects.addObject(Cls2, cnt, True, cnt.oneAggrValue)
                                End If
                                'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                    If parseCounterComputedObject(bo_computedobjects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ")," & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & "))", cnt) = False Then
                                        Return False
                                    End If
                                Else
                                    If parseCounterComputedObject(bo_computedobjects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ")," & LCase(cnt.oneAggrValue) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & "))", cnt) = False Then
                                        Return False
                                    End If
                                End If

                            ElseIf cnt.oneAggrFormula = True AndAlso cnt.oneAggrValue = "NONE" Then  'If Time Aggregation and Group Aggregation are same and Aggregation is None
                                Obj = bo_computedobjects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")", True, cnt.oneAggrValue)
                                If mt.PlainTable = True Then
                                    If parseCounterComputedObject(bo_computedobjects, Obj, Cls2, CMTechPack, "DC." & cnt.TypeName & "." & cnt.CounterName, cnt) = False Then
                                        Return False
                                    End If
                                ElseIf CMTechPack = True Then
                                    If parseCounterComputedObject(bo_computedobjects, Obj, Cls2, CMTechPack, "DC." & cnt.TypeName & "_RAW." & cnt.CounterName, cnt) = False Then
                                        Return False
                                    End If
                                Else
                                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                        If parseCounterComputedObject(bo_computedobjects, Obj, Cls2, CMTechPack, "@aggregate_aware(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ",DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ",DC." & cnt.TypeName & "_RAW." & cnt.CounterName & ")", cnt) = False Then
                                            Return False
                                        End If
                                    Else
                                        If parseCounterComputedObject(bo_computedobjects, Obj, Cls2, CMTechPack, "@aggregate_aware(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ",DC." & cnt.TypeName & "_RAW." & cnt.CounterName & ")", cnt) = False Then
                                            Return False
                                        End If
                                    End If
                                End If
                            ElseIf cnt.oneAggrFormula = False Then 'If Time Aggregation and Group Aggregation are different
                                For count = 0 To UBound(cnt.Aggregations)
                                    Obj = bo_computedobjects.addObject(Cls2, cnt, cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")", True, cnt.Aggregations(count))
                                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                        If parseCounterComputedObject(bo_computedobjects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAYBH." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & "))", cnt) = False Then
                                            Return False
                                        End If
                                    Else
                                        If parseCounterComputedObject(bo_computedobjects, Obj, Cls2, CMTechPack, "@aggregate_aware(" & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_DAY." & cnt.CounterName & ")," & LCase(cnt.Aggregations(count)) & "(DC." & cnt.TypeName & "_RAW." & cnt.CounterName & "))", cnt) = False Then
                                            Return False
                                        End If
                                    End If
                                Next count
                            Else
                                Trace.WriteLine("Universe Object '" & cnt.UnivObject & "' for Counter '" & cnt.CounterName & "' in Fact Table '" & cnt.TypeName & "' not added/modified")
                            End If
                        Else
                        End If

                        'Vector objects here
                        If cnt.CounterType = "VECTOR" AndAlso mt.VectorSupport = True Then
                            'Check for whether vector counter has range or not.
                            If (tpUtilities.isVectorRangePresent(cnt.MeasurementTypeID, cnt.CounterName, tpConn)) Then
                                vectorTable = Replace(cnt.TypeName, "DC_", "DIM_", , 1) & "_" & cnt.CounterName
                            For count = 1 To vector_rds.Count
                                rd = vector_rds.Item(count)
                                If vectorTable = rd.ReferenceTypeID AndAlso rd.UnivObject <> "" Then
                                    Cls2 = Cls.Classes.FindClass(mt.TypeName)

                                    HierarchyRootClass = mt.TypeName
                                    If cnt.UnivClass <> "" Then
                                        UsedClass = cnt.UnivClass
                                        ClassTree = Split(cnt.UnivClass, "//")
                                        For TreeCount = 0 To UBound(ClassTree)
                                            Cls2 = univ_classes.addClass(Univ, Cls.Classes.FindClass(HierarchyRootClass), ClassTree(TreeCount), ClassTree(TreeCount))
                                            HierarchyRootClass = ClassTree(TreeCount)
                                            UsedClass = ClassTree(TreeCount)
                                        Next TreeCount
                                    End If
                                    Obj = bo_computedobjects.addObject(Cls2, rd, False)
                                    If parseReferenceComputedObject(bo_computedobjects, Obj, Cls2, "DC." & rd.ReferenceTypeID & "." & rd.ReferenceDataID) = False Then
                                        Return False
                                    End If
                                End If
                            Next count
                            Else
                                ' Nothing
                                Trace.WriteLine("No range is defined for the type id " & cnt.MeasurementTypeID & " Counter: " & cnt.CounterName & ", not adding this vector computed counter")
                                Console.WriteLine("No range is defined for the type id " & cnt.MeasurementTypeID & " Counter: " & cnt.CounterName & ", not adding this vector computed counter")
                            End If

                        End If
                        'Vector
                    Next cnt_count

                End If

                If (EBSTechPack = True) Then
                    If (cnts.Count <> 0) Then
                        If cnt.UnivClass = "" Then
                            Obj = bo_computedobjects.removeComputedObject(Cls2, cnt.TypeName, cnts)
                        Else
                            Obj = bo_computedobjects.removeComputedObject(Cls2, cnt.UnivClass, cnts)
                        End If
                    Else
                        Obj = bo_computedobjects.removeComputedObject(Cls2, cnt.UnivClass, cnts)
                    End If
                End If
            End If
        Next mt_count
        Return True

    End Function
    Function parseCounterObject(ByRef objects As BOObjectsTPIde, ByRef Obj As Designer.Object, ByRef Cls As Designer.Class, _
                                ByRef CMTechPack As Boolean, ByRef SelectClause As String, ByVal cnt As CountersTPIde.Counter) As Boolean
        Dim result As Integer
        Obj.Select = SelectClause
        Obj.Format.NumberFormat = objects.formatCounterObject(Obj, cnt, CMTechPack)
        If counterParse = True Then
            result = objects.ParseCounterObject(Obj, Cls)
            If result = 0 Then
                counterParse = True
                Return True
            End If
            If result = 1 Then
                counterParse = False
                Return True
            End If
            If result = 2 Then
                Return False
            End If
        End If
        Return True
    End Function
    'JTS
    Function parseCounterComputedObject(ByRef objects As BOComputedObjectTPIde, ByRef Obj As Designer.Object, ByRef Cls As Designer.Class, _
                                        ByRef CMTechPack As Boolean, ByRef SelectClause As String, ByVal cnt As CountersTPIde.Counter) As Boolean
        Dim result As Integer
        Obj.Select = SelectClause
        Obj.Format.NumberFormat = objects.formatCounterObject(Obj, cnt, CMTechPack)
        If counterParse = True Then
            result = objects.ParseCounterObject(Obj, Cls)
            If result = 0 Then
                counterParse = True
                Return True
            End If
            If result = 1 Then
                counterParse = False
                Return True
            End If
            If result = 2 Then
                Return False
            End If
        End If
        Return True
    End Function
    Function parseReferenceObject(ByRef objects As BOObjectsTPIde, ByRef Obj As Designer.Object, ByRef Cls As Designer.Class, _
                                  ByRef SelectClause As String) As Boolean
        Dim result As Integer
        Obj.Select = SelectClause
        If referenceParse = True Then
            result = objects.ParseReferenceObject(Obj, Cls)
            If result = 0 Then
                referenceParse = True
                Return True
            End If
            If result = 1 Then
                referenceParse = False
                Return True
            End If
            If result = 2 Then
                Return False
            End If
        End If
        Return True
    End Function
    Function parseReferenceComputedObject(ByRef objects As BOComputedObjectTPIde, ByRef Obj As Designer.Object, ByRef Cls As Designer.Class, ByRef SelectClause As String) As Boolean
        Dim result As Integer
        Obj.Select = SelectClause
        If referenceParse = True Then
            result = objects.ParseReferenceObject(Obj, Cls)
            If result = 0 Then
                referenceParse = True
                Return True
            End If
            If result = 1 Then
                referenceParse = False
                Return True
            End If
            If result = 2 Then
                Return False
            End If
        End If
        Return True
    End Function

    ''
    ' Adds counter key objects to universe.
    '
    ' @param Univ Specifies reference to universe
    Function Universe_AddCounterKeys(ByRef Univ As Designer.Universe, ByRef mts As MeasurementTypesTPIde) As Boolean

        Dim Cls As Designer.Class
        Dim Cls2 As Designer.Class
        Dim Obj As Designer.Object
        Dim selectClause As String
        Dim count As Integer

        Dim universeProxy As IUniverseProxy = New UniverseProxy(Univ)
        Dim bo_objects As New BOObjectsTPIde(universeProxy)
        Dim univ_classes As New UnivClassesTPIde

        Console.WriteLine("Adds counter key objects to universe")
        For mt_count = 1 To mts.Count
            Try
            mt = mts.Item(mt_count)
            cnt_keys = mt.CounterKeys
            If mt.MeasurementTypeID <> "" Then
                If mt.RankTable = False Then
                    Cls = univ_classes.addClass(Univ, Univ.Classes.FindClass(mt.TypeName), mt.TypeName & "_Keys", "Keys for measurement " & mt.TypeName)
                    For cnt_key_count = 1 To cnt_keys.Count
                        cnt_key = cnt_keys.Item(cnt_key_count)
                        If cnt_key.UnivObject <> "" Then
                            Try
                                Obj = Cls.Objects.Item(cnt_key.UnivObject)
                            Catch e As Exception
                                Obj = Cls.Objects.Add(cnt_key.UnivObject, Cls)
                            End Try
                            UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                            If mt.PlainTable = True Then
                                selectClause = "DC." & cnt_key.TypeName & "." & cnt_key.CounterKeyName
                            End If
                            If mt.DayAggregation = False AndAlso mt.PlainTable = False Then
                                selectClause = "DC." & cnt_key.TypeName & "_RAW." & cnt_key.CounterKeyName
                            End If
                            'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                            If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                If mt.CreateCountTable = False Then
                                    If mt.DayAggregation = True Then
                                        selectClause = "@aggregate_aware(DC." & cnt_key.TypeName & "_DAY." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_DAYBH." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_RAW." & cnt_key.CounterKeyName & ")"
                                    Else
                                        selectClause = "@aggregate_aware(DC." & cnt_key.TypeName & "_DAYBH." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_RAW." & cnt_key.CounterKeyName & ")"
                                    End If
                                End If
                            Else
                                If mt.CreateCountTable = False Then
                                    If mt.DayAggregation = True Then
                                        selectClause = "@aggregate_aware(DC." & cnt_key.TypeName & "_DAY." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_RAW." & cnt_key.CounterKeyName & ")"
                                    Else
                                        selectClause = "DC." & cnt_key.TypeName & "_RAW." & cnt_key.CounterKeyName & ""
                                    End If
                                End If
                            End If

                            'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                            If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                'placeholder for extended COUNT functionality
                                If extendedCountObject = True Then
                                    If mt.CreateCountTable = True Then
                                        If mt.DayAggregation = True Then
                                            selectClause = "@aggregate_aware(DC." & cnt_key.TypeName & "_DAY." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_DAYBH." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_COUNT." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_DELTA." & cnt_key.CounterKeyName & ")"
                                        Else
                                            selectClause = "@aggregate_aware(DC." & cnt_key.TypeName & "_DAYBH." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_COUNT." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_DELTA." & cnt_key.CounterKeyName & ")"
                                        End If

                                    End If
                                    'placeholder for extended COUNT functionality
                                Else
                                    If mt.CreateCountTable = True Then
                                        If mt.DayAggregation = True Then
                                            selectClause = "@aggregate_aware(DC." & cnt_key.TypeName & "_DAY." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_DAYBH." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_COUNT." & cnt_key.CounterKeyName & ")"
                                        Else
                                            selectClause = "@aggregate_aware(DC." & cnt_key.TypeName & "_DAYBH." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_COUNT." & cnt_key.CounterKeyName & ")"
                                        End If

                                    End If
                                End If
                            Else
                                'placeholder for extended COUNT functionality
                                If extendedCountObject = True Then
                                    If mt.CreateCountTable = True Then
                                        If mt.DayAggregation = True Then
                                            selectClause = "@aggregate_aware(DC." & cnt_key.TypeName & "_DAY." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_COUNT." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_DELTA." & cnt_key.CounterKeyName & ")"
                                        Else
                                            selectClause = "@aggregate_aware(DC." & cnt_key.TypeName & "_COUNT." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_DELTA." & cnt_key.CounterKeyName & ")"
                                        End If

                                    End If
                                    'placeholder for extended COUNT functionality
                                Else
                                    If mt.CreateCountTable = True Then
                                        If mt.DayAggregation = True Then
                                            selectClause = "@aggregate_aware(DC." & cnt_key.TypeName & "_DAY." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_COUNT." & cnt_key.CounterKeyName & ")"
                                        Else
                                            selectClause = "DC." & cnt_key.TypeName & "_COUNT." & cnt_key.CounterKeyName & ""
                                        End If

                                    End If
                                End If

                            End If

                            bo_objects.keyFormat(Obj, cnt_key)
                            If parseReferenceObject(bo_objects, Obj, Cls, selectClause) = False Then
                                Return False
                            End If

                            If mt.CreateCountTable = True Then
                                Cls2 = univ_classes.addClass(Univ, Univ.Classes.FindClass(mt.TypeName & "_RAW"), mt.TypeName & "_RAW_Keys", "Keys for measurement " & mt.TypeName)
                                Try
                                    Obj = Cls2.Objects.Item(cnt_key.UnivObject)
                                Catch e As Exception
                                    Obj = Cls2.Objects.Add(cnt_key.UnivObject, Cls2)
                                End Try
                                UniverseFunctionsTPIde.updatedObjects &= Cls2.Name & "/" & Obj.Name & ";"
                                bo_objects.keyFormat(Obj, cnt_key)
                                If parseReferenceObject(bo_objects, Obj, Cls2, "DC." & cnt_key.TypeName & "_RAW." & cnt_key.CounterKeyName) = False Then
                                    Return False
                                End If

                            End If
                        End If
                    Next cnt_key_count
                    'Vector Index object
                    For count = 1 To mt.Counters.Count
                        If mt.Counters.Item(count).CounterType = "VECTOR" Then

                            '  Try
                            ' Obj = Cls2.Objects.Item("Vector Index")
                            'Catch e As Exception
                            '   Obj = Cls2.Objects.Add("Vector Index", Cls2)
                            '  End Try

                            ' Try
                            'cnt_key.CounterKeyName = "Vector"
                            'UniverseFunctions.updatedObjects &= Cls2.Name & "/" & Obj.Name & ";"
                            'bo_objects.keyFormat(Obj, cnt_key)

                            'selectClause = "DC." & mt.MeasurementTypeID & "_RAW." & "DCVECTOR_INDEX"
                            'Obj.Description = "Vector Index"
                            'bo_objects.setObjectType("integer", Obj, False)
                            'Obj.Qualification = Designer.DsObjectQualification.dsDimensionObject
                            'Obj.Format.NumberFormat = bo_objects.formatObject(Obj)
                            'Obj.HasListOfValues = True
                            'Obj.AllowUserToEditLov = True
                            'Obj.AutomaticLovRefreshBeforeUse = False
                            'Obj.ExportLovWithUniverse = False
                            'If parseReferenceObject(bo_objects, Obj, Cls2, selectClause) = False Then
                            '     Return False
                            'End If

                            ' Catch ex As Exception
                            ' Trace.WriteLine("Counter Key Error: " & ex.Message)

                            'End Try

                            Try
                                Obj = Cls.Objects.Item("Vector Index")
                            Catch e As Exception
                                Obj = Cls.Objects.Add("Vector Index", Cls)
                            End Try
                            UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                            If mt.PlainTable = True Then
                                selectClause = "DC." & mt.TypeName & "." & "DCVECTOR_INDEX"
                            End If
                            If mt.DayAggregation = False AndAlso mt.PlainTable = False Then
                                selectClause = "DC." & mt.TypeName & "_RAW." & "DCVECTOR_INDEX"
                            End If

                            'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                            If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                If mt.CreateCountTable = False Then
                                    If mt.DayAggregation = True Then
                                        selectClause = "@aggregate_aware(DC." & mt.TypeName & "_DAY." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_DAYBH." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_RAW." & "DCVECTOR_INDEX" & ")"
                                    Else
                                        selectClause = "@aggregate_aware(DC." & mt.TypeName & "_DAYBH." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_RAW." & "DCVECTOR_INDEX" & ")"
                                    End If

                                End If
                            Else
                                If mt.CreateCountTable = False Then
                                    If mt.DayAggregation = True Then
                                        selectClause = "@aggregate_aware(DC." & mt.TypeName & "_DAY." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_RAW." & "DCVECTOR_INDEX" & ")"
                                    Else
                                        selectClause = "DC." & mt.TypeName & "_RAW." & "DCVECTOR_INDEX" & ""
                                    End If

                                End If
                            End If

                            'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                            If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                'placeholder for extended COUNT functionality
                                If extendedCountObject = True Then
                                    If mt.CreateCountTable = True Then
                                        If mt.DayAggregation = True Then
                                            selectClause = "@aggregate_aware(DC." & mt.TypeName & "_DAY." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_DAYBH." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_COUNT." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_DELTA." & "DCVECTOR_INDEX" & ")"
                                        Else
                                            selectClause = "@aggregate_aware(DC." & mt.TypeName & "_DAYBH." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_COUNT." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_DELTA." & "DCVECTOR_INDEX" & ")"
                                        End If

                                    End If
                                    'placeholder for extended COUNT functionality
                                Else
                                    If mt.CreateCountTable = True Then
                                        If mt.DayAggregation = True Then
                                            selectClause = "@aggregate_aware(DC." & mt.TypeName & "_DAY." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_DAYBH." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_COUNT." & "DCVECTOR_INDEX" & ")"
                                        Else
                                            selectClause = "@aggregate_aware(DC." & mt.TypeName & "_DAYBH." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_COUNT." & "DCVECTOR_INDEX" & ")"
                                        End If

                                    End If
                                End If
                            Else
                                'placeholder for extended COUNT functionality
                                If extendedCountObject = True Then
                                    If mt.CreateCountTable = True Then
                                        If mt.DayAggregation = True Then
                                            selectClause = "@aggregate_aware(DC." & mt.TypeName & "_DAY." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_COUNT." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_DELTA." & "DCVECTOR_INDEX" & ")"
                                        Else
                                            selectClause = "@aggregate_aware(DC." & mt.TypeName & "_COUNT." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_DELTA." & "DCVECTOR_INDEX" & ")"
                                        End If

                                    End If
                                    'placeholder for extended COUNT functionality
                                Else
                                    If mt.DayAggregation = True AndAlso mt.CreateCountTable = True Then
                                        If mt.DayAggregation = True Then
                                            selectClause = "@aggregate_aware(DC." & mt.TypeName & "_DAY." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_COUNT." & "DCVECTOR_INDEX" & ")"
                                        Else
                                            selectClause = "DC." & mt.TypeName & "_COUNT." & "DCVECTOR_INDEX" & ""
                                        End If

                                    End If
                                End If
                            End If

                            Try
                                Obj.Description = "Vector Index"
                                bo_objects.setObjectType("integer", Obj, False)
                                Obj.Qualification = Designer.DsObjectQualification.dsDimensionObject
                                Obj.Format.NumberFormat = bo_objects.formatObject(Obj)
                                Obj.HasListOfValues = True
                                Obj.AllowUserToEditLov = True
                                Obj.AutomaticLovRefreshBeforeUse = False
                                Obj.ExportLovWithUniverse = False
                                If parseReferenceObject(bo_objects, Obj, Cls, selectClause) = False Then
                                    Return False
                                End If
                            Catch ex As Exception
                                Trace.WriteLine("Counter Key Error: " & ex.Message)
                            End Try

                            Exit For
                        End If
                    Next count

                    If FullAware = False Then
                        If mt.ObjectBusyHours <> "" Then
                            Cls = univ_classes.addClass(Univ, Univ.Classes.FindClass(mt.MeasurementTypeID + "_BH"), mt.MeasurementTypeID + "_BH_Keys", "Keys for measurement " & mt.MeasurementTypeID & " for busy hours")

                            For cnt_key_count = 1 To cnt_keys.Count
                                cnt_key = cnt_keys.Item(cnt_key_count)
                                If cnt_key.UnivObject <> "" Then
                                    Try
                                        Obj = Cls.Objects.Item(cnt_key.UnivObject)
                                    Catch e As Exception
                                        Obj = Cls.Objects.Add(cnt_key.UnivObject, Cls)
                                    End Try
                                    UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                                    bo_objects.keyFormat(Obj, cnt_key)
                                    If parseReferenceObject(bo_objects, Obj, Cls, "DC." & cnt_key.MeasurementTypeID & "_DAYBH." & cnt_key.CounterKeyName) = False Then
                                        Return False
                                    End If

                                End If
                            Next cnt_key_count

                            'Vector Index object
                            For count = 1 To mt.Counters.Count
                                If mt.Counters.Item(count).CounterType = "VECTOR" Then
                                    Try
                                        Obj = Cls.Objects.Item("Vector Index")
                                    Catch e As Exception
                                        Obj = Cls.Objects.Add("Vector Index", Cls)
                                    End Try
                                    UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                                    Try
                                        Obj.Description = "Vector Index"
                                        bo_objects.setObjectType("integer", Obj, False)
                                        Obj.Qualification = Designer.DsObjectQualification.dsDimensionObject
                                        Obj.Format.NumberFormat = bo_objects.formatObject(Obj)
                                        Obj.HasListOfValues = True
                                        Obj.AllowUserToEditLov = True
                                        Obj.AutomaticLovRefreshBeforeUse = False
                                        Obj.ExportLovWithUniverse = False
                                        If parseReferenceObject(bo_objects, Obj, Cls, "DC." & mt.MeasurementTypeID & "_DAYBH." & "DCVECTOR_INDEX") = False Then
                                            Return False
                                        End If
                                    Catch ex As Exception
                                        Trace.WriteLine("Counter Key Error: " & ex.Message)
                                    End Try
                                    Exit For
                                End If
                            Next count
                        End If
                    End If
                End If
            End If
            Catch ex As Exception
                Trace.WriteLine("Error adding counter keys to universe: " & ex.ToString())
            End Try
        Next mt_count
        Return True

    End Function
    ''
    ' Adds computed counter key objects to universe.
    '
    ' @param Univ Specifies reference to universe
    Function Universe_AddComputedCounterKeys(ByRef Univ As Designer.Universe, ByRef mts As MeasurementTypesTPIde) As Boolean

        Dim Cls As Designer.Class
        Dim Cls2 As Designer.Class
        Dim Obj As Designer.Object
        Dim selectClause As String
        Dim count As Integer

        Dim bo_computedobjects As New BOComputedObjectTPIde
        Dim univ_classes As New UnivClassesTPIde

        For mt_count = 1 To mts.Count

            mt = mts.Item(mt_count)
            cnt_keys = mt.CounterKeys
            If mt.MeasurementTypeID <> "" Then
                If mt.RankTable = False Then
                    Cls = univ_classes.addClass(Univ, Univ.Classes.FindClass(mt.TypeName), mt.TypeName & "_Keys", "Keys for measurement " & mt.TypeName)
                    For cnt_key_count = 1 To cnt_keys.Count
                        cnt_key = cnt_keys.Item(cnt_key_count)
                        If cnt_key.UnivObject <> "" Then
                            Try
                                Obj = Cls.Objects.Item(cnt_key.UnivObject)
                            Catch e As Exception
                                Obj = Cls.Objects.Add(cnt_key.UnivObject, Cls)
                            End Try
                            UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                            If mt.PlainTable = True Then
                                selectClause = "DC." & cnt_key.TypeName & "." & cnt_key.CounterKeyName
                            End If
                            If mt.DayAggregation = False AndAlso mt.PlainTable = False Then
                                selectClause = "DC." & cnt_key.TypeName & "_RAW." & cnt_key.CounterKeyName
                            End If
                            'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                            If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = False Then
                                    selectClause = "@aggregate_aware(DC." & cnt_key.TypeName & "_DAY." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_DAYBH." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_RAW." & cnt_key.CounterKeyName & ")"
                                End If
                            Else
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = False Then
                                    selectClause = "@aggregate_aware(DC." & cnt_key.TypeName & "_DAY." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_RAW." & cnt_key.CounterKeyName & ")"
                                End If
                            End If

                            'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                            If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = True Then
                                    selectClause = "@aggregate_aware(DC." & cnt_key.TypeName & "_DAY." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_DAYBH." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_COUNT." & cnt_key.CounterKeyName & ")"
                                End If
                            Else
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = True Then
                                    selectClause = "@aggregate_aware(DC." & cnt_key.TypeName & "_DAY." & cnt_key.CounterKeyName & ",DC." & cnt_key.TypeName & "_COUNT." & cnt_key.CounterKeyName & ")"
                                End If
                            End If

                            bo_computedobjects.keyFormat(Obj, cnt_key)
                            If parseReferenceComputedObject(bo_computedobjects, Obj, Cls, selectClause) = False Then
                                Return False
                            End If

                            If mt.DayAggregation = True AndAlso mt.CreateCountTable = True Then
                                Cls2 = univ_classes.addClass(Univ, Univ.Classes.FindClass(mt.TypeName & "_RAW"), mt.TypeName & "_RAW_Keys", "Keys for measurement " & mt.TypeName)
                                Try
                                    Obj = Cls2.Objects.Item(cnt_key.UnivObject)
                                Catch e As Exception
                                    Obj = Cls2.Objects.Add(cnt_key.UnivObject, Cls2)
                                End Try
                                UniverseFunctionsTPIde.updatedObjects &= Cls2.Name & "/" & Obj.Name & ";"
                                bo_computedobjects.keyFormat(Obj, cnt_key)
                                If parseReferenceComputedObject(bo_computedobjects, Obj, Cls2, "DC." & cnt_key.TypeName & "_RAW." & cnt_key.CounterKeyName) = False Then
                                    Return False
                                End If

                            End If
                        End If
                    Next cnt_key_count
                    'Vector Index object
                    For count = 1 To mt.Counters.Count
                        If mt.Counters.Item(count).CounterType = "VECTOR" Then

                            '  Try
                            ' Obj = Cls2.Objects.Item("Vector Index")
                            'Catch e As Exception
                            '   Obj = Cls2.Objects.Add("Vector Index", Cls2)
                            '  End Try

                            ' Try
                            'cnt_key.CounterKeyName = "Vector"
                            'UniverseFunctions.updatedObjects &= Cls2.Name & "/" & Obj.Name & ";"
                            'bo_objects.keyFormat(Obj, cnt_key)

                            'selectClause = "DC." & mt.MeasurementTypeID & "_RAW." & "DCVECTOR_INDEX"
                            'Obj.Description = "Vector Index"
                            'bo_objects.setObjectType("integer", Obj, False)
                            'Obj.Qualification = Designer.DsObjectQualification.dsDimensionObject
                            'Obj.Format.NumberFormat = bo_objects.formatObject(Obj)
                            'Obj.HasListOfValues = True
                            'Obj.AllowUserToEditLov = True
                            'Obj.AutomaticLovRefreshBeforeUse = False
                            'Obj.ExportLovWithUniverse = False
                            'If parseReferenceObject(bo_objects, Obj, Cls2, selectClause) = False Then
                            '     Return False
                            'End If

                            ' Catch ex As Exception
                            ' Trace.WriteLine("Counter Key Error: " & ex.Message)

                            'End Try

                            Try
                                Obj = Cls.Objects.Item("Vector Index")
                            Catch e As Exception
                                Obj = Cls.Objects.Add("Vector Index", Cls)
                            End Try
                            UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                            If mt.PlainTable = True Then
                                selectClause = "DC." & mt.TypeName & "." & "DCVECTOR_INDEX"
                            End If
                            If mt.DayAggregation = False AndAlso mt.PlainTable = False Then
                                selectClause = "DC." & mt.TypeName & "_RAW." & "DCVECTOR_INDEX"
                            End If

                            'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                            If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = False Then
                                    selectClause = "@aggregate_aware(DC." & mt.TypeName & "_DAY." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_DAYBH." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_RAW." & "DCVECTOR_INDEX" & ")"
                                End If
                            Else
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = False Then
                                    selectClause = "@aggregate_aware(DC." & mt.TypeName & "_DAY." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_RAW." & "DCVECTOR_INDEX" & ")"
                                End If
                            End If

                            'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                            If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = True Then
                                    selectClause = "@aggregate_aware(DC." & mt.TypeName & "_DAY." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_DAYBH." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_COUNT." & "DCVECTOR_INDEX" & ")"
                                End If
                            Else
                                If mt.DayAggregation = True AndAlso mt.CreateCountTable = True Then
                                    selectClause = "@aggregate_aware(DC." & mt.TypeName & "_DAY." & "DCVECTOR_INDEX" & ",DC." & mt.TypeName & "_COUNT." & "DCVECTOR_INDEX" & ")"
                                End If
                            End If

                            Try
                                Obj.Description = "Vector Index"
                                bo_computedobjects.setObjectType("integer", Obj, False)
                                Obj.Qualification = Designer.DsObjectQualification.dsDimensionObject
                                Obj.Format.NumberFormat = bo_computedobjects.formatObject(Obj)
                                Obj.HasListOfValues = True
                                Obj.AllowUserToEditLov = True
                                Obj.AutomaticLovRefreshBeforeUse = False
                                Obj.ExportLovWithUniverse = False
                                If parseReferenceComputedObject(bo_computedobjects, Obj, Cls, selectClause) = False Then
                                    Return False
                                End If
                            Catch ex As Exception
                                Trace.WriteLine("Counter Key Error: " & ex.Message)
                            End Try

                            Exit For
                        End If
                    Next count

                    If FullAware = False Then
                        If mt.ObjectBusyHours <> "" Then
                            Cls = univ_classes.addClass(Univ, Univ.Classes.FindClass(mt.MeasurementTypeID + "_BH"), mt.MeasurementTypeID + "_BH_Keys", "Keys for measurement " & mt.MeasurementTypeID & " for busy hours")

                            For cnt_key_count = 1 To cnt_keys.Count
                                cnt_key = cnt_keys.Item(cnt_key_count)
                                If cnt_key.UnivObject <> "" Then
                                    Try
                                        Obj = Cls.Objects.Item(cnt_key.UnivObject)
                                    Catch e As Exception
                                        Obj = Cls.Objects.Add(cnt_key.UnivObject, Cls)
                                    End Try
                                    UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                                    bo_computedobjects.keyFormat(Obj, cnt_key)
                                    If parseReferenceComputedObject(bo_computedobjects, Obj, Cls, "DC." & cnt_key.MeasurementTypeID & "_DAYBH." & cnt_key.CounterKeyName) = False Then
                                        Return False
                                    End If

                                End If
                            Next cnt_key_count

                            'Vector Index object
                            For count = 1 To mt.Counters.Count
                                If mt.Counters.Item(count).CounterType = "VECTOR" Then
                                    Try
                                        Obj = Cls.Objects.Item("Vector Index")
                                    Catch e As Exception
                                        Obj = Cls.Objects.Add("Vector Index", Cls)
                                    End Try
                                    UniverseFunctionsTPIde.updatedObjects &= Cls.Name & "/" & Obj.Name & ";"
                                    Try
                                        Obj.Description = "Vector Index"
                                        bo_computedobjects.setObjectType("integer", Obj, False)
                                        Obj.Qualification = Designer.DsObjectQualification.dsDimensionObject
                                        Obj.Format.NumberFormat = bo_computedobjects.formatObject(Obj)
                                        Obj.HasListOfValues = True
                                        Obj.AllowUserToEditLov = True
                                        Obj.AutomaticLovRefreshBeforeUse = False
                                        Obj.ExportLovWithUniverse = False
                                        If parseReferenceComputedObject(bo_computedobjects, Obj, Cls, "DC." & mt.MeasurementTypeID & "_DAYBH." & "DCVECTOR_INDEX") = False Then
                                            Return False
                                        End If
                                    Catch ex As Exception
                                        Trace.WriteLine("Counter Key Error: " & ex.Message)
                                    End Try
                                    Exit For
                                End If
                            Next count
                        End If
                    End If
                End If
            End If
        Next mt_count
        Return True

    End Function


    ''
    ' Adds classes to universe.
    '
    ' @param Univ Specifies reference to universe
    Private Function Universe_AddClasses(ByRef Univ As Object, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, ByRef NewUniverse As Boolean, ByRef UniverseNameExtension As String) As Boolean
        Dim Cls As Designer.Class
        Dim addClass As Boolean
        Console.WriteLine("Adds classes to universe")
        For univ_cls_count = 1 To univ_clss.Count
            Try
            univ_cls = univ_clss.Item(univ_cls_count)
            addClass = False

            If univ_cls.UniverseExtension = "all" Then
                addClass = True
            ElseIf univ_cls.UniverseExtension = "" AndAlso UniverseNameExtension = "" Then
                addClass = True
            Else
                Dim UniverseCountList() As String
                Dim UnvCount As Integer
                If InStrRev(univ_cls.UniverseExtension, ",") = 0 Then
                    If univ_cls.UniverseExtension = UniverseNameExtension Then
                        addClass = True
                    End If
                Else
                    UniverseCountList = Split(univ_cls.UniverseExtension, ",")
                    For UnvCount = 0 To UBound(UniverseCountList)
                        If UniverseCountList(UnvCount) = UniverseNameExtension Then
                            addClass = True
                            Exit For
                        End If
                    Next
                End If
            End If

            If addClass = True Then
                If univ_cls.ObjectBHRelated = ObjectBHSupport OrElse univ_cls.ElementBHRelated = ElementBHSupport OrElse (univ_cls.ObjectBHRelated = False AndAlso univ_cls.ElementBHRelated = False) Then
                    If univ_cls.ParentClassName = "Root" Then
                        If univ_clss.addRootClass(Univ, univ_cls, NewUniverse) = False Then
                            Return False
                        End If
                    Else
                        If univ_clss.addChildClass(Univ, univ_cls, NewUniverse) = False Then
                            Return False
                        End If
                    End If
                ElseIf univ_cls.ObjectBHRelated = True AndAlso ObjectBHSupport = False AndAlso univ_cls.ElementBHRelated = True AndAlso ElementBHSupport = False Then
                    'Do nothing
                Else
                    If univ_cls.ParentClassName = "Root" Then
                        If univ_clss.addRootClass(Univ, univ_cls, NewUniverse) = False Then
                            Return False
                        End If
                    Else
                        If univ_clss.addChildClass(Univ, univ_cls, NewUniverse) = False Then
                            Return False
                        End If
                    End If
                End If
            End If
            Catch ex As Exception
                Trace.WriteLine("Error adding class to universe: " & ex.ToString())
            End Try
        Next univ_cls_count
        Return True
    End Function

    ''
    ' Adds extra objects to universe.
    '
    ' @param Univ Specifies reference to universe
    ' @remarks Extra objects are defined in TP definition's sheet 'Universe objects'.
    Private Function Universe_AddObjects(ByRef Univ As Object, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, ByRef mts As MeasurementTypesTPIde, ByRef rds As ReferenceDatasTPIde, ByRef UniverseNameExtension As String, ByRef TechPackTPIde As String) As Boolean
        Dim count As Integer

        bo_objects = New BOObjectsTPIde(New UniverseProxy(Univ))
        bo_objects.ObjectParse = objectParse
        'Modified for TR HK80815
        Console.WriteLine("Adds extra Objects to universe")
        'for TR HK80815
        If bo_objects.getObjectsFromDatabase(TechPackName, TPVersion, tpConn, dbCommand, dbReader, mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde, True) = False Then
            Return False
        End If
        Console.WriteLine("Adds Base Techpack's objects to universe")
        If bo_objects.getObjectsFromDatabase(TechPackName, TPVersion, baseConn, dbCommand, dbReader, mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, BaseTechPackTPIde, True) = False Then
            Return False
        End If

        If (rankBusyHourFunctionality = True) Then
            If bo_objects.addBusyHourRankObjects(mts) = False Then
                Return False
            End If
        End If

        'reference objects
        For count = 1 To rds.Count
            rd = rds.Item(count)
            If rd.UnivObject <> "" AndAlso rd.UnivClass <> "" Then
                If bo_objects.addObject(rd.UnivClass, rd.UnivObject, rd.Datatype, "DC." & rd.TypeName & "." & rd.ReferenceDataID, rd.Description) = False Then
                    Return False
                End If
            End If
        Next count
        Return True

    End Function


    ''
    ' Adds extra computed objects to universe.
    '
    ' @param Univ Specifies reference to universe
    ' @remarks Extra objects are defined in TP definition's sheet 'Universe objects'.
    Private Function Universe_AddComputedObjects(ByRef Univ As Object, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, ByRef mts As MeasurementTypesTPIde, ByRef rds As ReferenceDatasTPIde, ByRef UniverseNameExtension As String, ByRef TechPackTPIde As String) As Boolean
        Dim count As Integer

        bo_computedobjects = New BOComputedObjectTPIde
        bo_computedobjects.ObjectParse = objectParse
        'Modified for TR HK80815
        Console.WriteLine("Adds extra computed Objects to universe")
        'for TR HK80815
        If bo_computedobjects.addObjects(TechPackName, TPVersion, Univ, tpConn, dbCommand, dbReader, mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde, BaseTechPackTPIde) = False Then
            Return False
        End If

        If bo_computedobjects.addObjects(TechPackName, TPVersion, Univ, baseConn, dbCommand, dbReader, mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, BaseTechPackTPIde, BaseTechPackTPIde) = False Then
            Return False
        End If

        For count = 1 To rds.Count
            rd = rds.Item(count)
            If rd.UnivObject <> "" AndAlso rd.UnivClass <> "" Then
                If bo_computedobjects.addObject(Univ, rd.UnivClass, rd.UnivObject, rd.Datatype, "DC." & rd.TypeName & "." & rd.ReferenceDataID, rd.Description) = False Then
                    Return False
                End If
            End If
        Next count
        Return True

    End Function


    ''
    ' Adds extra conditions to universe.
    '
    ' @param Univ Specifies reference to universe
    ' @remarks Extra conditions are defined in TP definition's sheet 'Universe conditions'.
    Private Function Universe_AddConditions(ByRef Univ As Designer.Universe, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, _
                                             ByRef mts As MeasurementTypesTPIde, ByRef rds As ReferenceDatasTPIde, _
                                            ByRef UniverseNameExtension As String) As Boolean
        Dim count As Integer

        Dim universeProxy As IUniverseProxy = New UniverseProxy(Univ)
        bo_conditions = New BOConditionsTPIde(universeProxy)
        bo_conditions.ConditionParse = conditionParse

            Console.WriteLine("Adds extra conditions to universe")
        If bo_conditions.addConditions(TechPackName, tpConn, dbCommand, dbReader, mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, TechPackTPIde) = False Then
            Return False
        End If
        Console.WriteLine("Adds Base tech pack's conditions to universe")
        If bo_conditions.addConditions(TechPackName, baseConn, dbCommand, dbReader, mts, ObjectBHSupport, ElementBHSupport, UniverseNameExtension, BaseTechPackTPIde) = False Then
            Return False
        End If

        If (rankBusyHourFunctionality = True) Then
            ' Add conditions for new busy hour objects
            If bo_conditions.addBusyHourConditions(mts) = False Then
                Console.WriteLine("Error adding busy hour conditions to universe")
                Return False
            End If
        End If

        Console.WriteLine("Adds busy hour conditions to universe")
        'reference conditions
        For count = 1 To rds.Count
            rd = rds.Item(count)
            If rd.UnivObject <> "" AndAlso rd.UnivClass <> "" AndAlso rd.UnivCondition = True Then
                If bo_conditions.addCondition(rd.UnivClass, rd.UnivObject, rd.Description) = False Then
                    ' Write error to log but continue adding conditions:
                    Trace.WriteLine("Error adding reference condition for: " & rd.UnivClass & "\" & rd.UnivObject)
                End If
            End If
        Next count

        Return True
    End Function

    ''
    ' Adds tables to universe. 
    '@param Univ Specifies reference to universe
    Private Function Universe_AddTables(ByRef Univ As Designer.Universe, ByRef ObjectBHSupport As Boolean, ByRef ElementBHSupport As Boolean, _
                                        ByRef NewUniverse As Boolean, ByRef mts As MeasurementTypesTPIde, _
                                        ByRef rts As ReferenceTypesTPIde, ByRef vector_rts As ReferenceTypesTPIde, _
                                        ByRef UniverseNameExtension As String) As Boolean
        'modified /added for TR HK 80515
        Console.WriteLine("Adding tables to universe: " & Univ.LongName)

        Dim Tbl As Designer.Table
        Dim bo_tables As New BOTablesTPIde
        Console.WriteLine("Adding tech pack's tables to universe: " & Univ.LongName)
        If bo_tables.addTables(Univ, tpConn, dbCommand, dbReader, ObjectBHSupport, ElementBHSupport, NewUniverse, UniverseNameExtension, TechPackTPIde) = False Then
            Return False
        End If
        Console.WriteLine("Adding base tech pack's tables to universe: " & Univ.LongName)
        If bo_tables.addTables(Univ, baseConn, dbCommand, dbReader, ObjectBHSupport, ElementBHSupport, NewUniverse, UniverseNameExtension, BaseTechPackTPIde) = False Then
            Return False
        End If

        For rt_count = 1 To rts.Count
            rt = rts.Item(rt_count)
            'If rt.IncludeInUniverse = True Then
            If bo_tables.addTable(Univ, rt.TypeName, NewUniverse) = False Then
                Return False
            End If
            'End If
        Next rt_count

        For rt_count = 1 To vector_rts.Count
            rt = vector_rts.Item(rt_count)
            'If rt.IncludeInUniverse = True Then
            If bo_tables.addTable(Univ, rt.TypeName, NewUniverse) = False Then
                Return False
            End If
            'End If
        Next rt_count

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" Then
                If mt.RankTable = False Then
                    If mt.PlainTable = False Then
                        If bo_tables.addTable(Univ, mt.TypeName & "_RAW", NewUniverse) = False Then
                            Return False
                        End If
                    End If
                    If mt.PlainTable = True Then
                        If bo_tables.addTable(Univ, mt.TypeName, NewUniverse) = False Then
                            Return False
                        End If
                    End If
                    If mt.CreateCountTable = True Then
                        If bo_tables.addTable(Univ, mt.TypeName & "_COUNT", NewUniverse) = False Then
                            Return False
                        End If
                        'placeholder for extended COUNT functionality
                        If extendedCountObject = True Then
                            If bo_tables.addTable(Univ, mt.TypeName & "_DELTA", NewUniverse) = False Then
                                Return False
                            End If
                        End If
                        'placeholder for extended COUNT functionality
                    End If
                    If mt.DayAggregation = True Then
                        If bo_tables.addTable(Univ, mt.TypeName & "_DAY", NewUniverse) = False Then
                            Return False
                        End If
                    End If
                    If mt.ObjectBusyHours <> "" Then
                        If bo_tables.addTable(Univ, mt.TypeName & "_DAYBH", NewUniverse) = False Then
                            Return False
                        End If
                    End If
                Else
                    If mt.ElementBusyHours = True Or mt.RankTable = True Then
                        'If (rankBusyHourFunctionality = True) Then
                            If bo_tables.addTable(Univ, mt.TypeName & "_RANKBH", NewUniverse) = False Then
                                Console.WriteLine("Error adding table " & mt.TypeName & "_RANKBH")
                            Return False
                            End If
                        'End If
                    End If
                End If
            End If
        Next mt_count

        Return True

    End Function

    Private Function getObjectBHSupport(ByRef mts As MeasurementTypesTPIde) As Boolean

        Dim Found As Boolean
        Found = False
        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" Then
                If mt.RankTable = True Then
                    If mt.ObjectBusyHours <> "" Then
                        Found = True
                        Exit For
                    End If
                End If
            End If
        Next mt_count

        Return Found

    End Function

    Private Function getElementBHSupport(ByRef mts As MeasurementTypesTPIde) As Boolean

        Dim Found As Boolean
        Found = False
        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" Then
                If mt.RankTable = True Then
                    If mt.ElementBusyHours = True Then
                        Found = True
                        Exit For
                    End If
                End If
            End If
        Next mt_count

        Return Found

    End Function

    ''
    ' Adds incompatible objects and conditions to universe. 
    ' Objects are: 
    ' - Time/Hour to DAY measurement tables
    ' - Element (Busy Hour)/Element Name to DAY measurement tables
    ' Conditions are: 
    ' - Element (Busy Hour)/Select Element Name to DAY measurement tables
    '
    ' @param Univ Specifies reference to universe
    Private Sub Universe_AddIncompatibleObjects(ByRef Univ As Designer.Universe, ByRef mts As MeasurementTypesTPIde)

        Dim Obj As Designer.Object

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)

            If mt.RankTable = False Then
                If mt.DayAggregation = True Then
                    addInCompatibleObject(Univ, "DC." & mt.TypeName & "_DAY", "Time", "Hour")
                End If

                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                    addInCompatibleObject(Univ, "DC." & mt.TypeName & "_DAYBH", "Time", "Hour")
                    If mt.DayAggregation = True Then
                        addInCompatibleObject(Univ, "DC." & mt.TypeName & "_DAY", "Busy Hour", "Busy Hour")
                    End If
                End If
            End If
            If mt.RankTable = False AndAlso mt.ElementBusyHours = True Then
                If mt.DayAggregation = True Then
                    addInCompatibleObject(Univ, "DC." & mt.TypeName & "_DAY", "Element (Busy Hour)", "Element Name")
                    addInCompatibleCondition(Univ, "DC." & mt.TypeName & "_DAY", "Element (Busy Hour)", "Select Element Name")
                End If
                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                    addInCompatibleObject(Univ, "DC." & mt.TypeName & "_DAYBH", "Element (Busy Hour)", "Element Name")
                    addInCompatibleCondition(Univ, "DC." & mt.TypeName & "_DAYBH", "Element (Busy Hour)", "Select Element Name")
                End If
            End If
        Next mt_count

        'placeholder for extended COUNT functionality
        If extendedCountObject = True Then
            For mt_count = 1 To mts.Count
                mt = mts.Item(mt_count)
                If mt.RankTable = False AndAlso mt.CreateCountTable = True Then
                    addInCompatibleCondition(Univ, "DC." & mt.TypeName & "_COUNT", "General", "Immediate Delta Calculation")
                End If
            Next mt_count
        End If
        'placeholder for extended COUNT functionality

    End Sub
    'placeholder for extended COUNT functionality
    Private Sub addInCompatibleObject(ByRef Univ As Designer.Universe, ByRef tableName As String, ByRef className As String, ByRef objectName As String)
        Dim Obj As Designer.Object
        Try
            Obj = Univ.Tables(tableName).IncompatibleObjects.Item(objectName)
        Catch e As Exception
            Try
                Obj = Univ.Tables(tableName).IncompatibleObjects.Add(objectName, className)
            Catch ex As Exception
                Trace.WriteLine("Incompatible Object '" & className & "/" & objectName & "' adding failed for Table'" & tableName & "'.")
                Trace.WriteLine("Incompatible Object exception: " & ex.ToString)
            End Try
        End Try
    End Sub
    'placeholder for extended COUNT functionality    'placeholder for extended COUNT functionality
    Private Sub addInCompatibleCondition(ByRef Univ As Designer.Universe, ByRef tableName As String, ByRef className As String, ByRef conditionName As String)
        Dim Obj As Designer.Object
        Try
            Obj = Univ.Tables(tableName).IncompatiblePredefConditions.Item(conditionName)
        Catch e As Exception
            Try
                Try
                    Univ.Tables(tableName).IncompatiblePredefConditions.Add(conditionName, className)
                Catch ex As Exception
                    Trace.WriteLine("Incompatible Condition '" & className & "/" & conditionName & "' adding failed for Table'" & tableName & "'.")
                    Trace.WriteLine("Incompatible Condition exception: " & ex.ToString)
                End Try
            Catch ex As Exception
                Try
                    Univ.Tables(tableName).IncompatiblePredefConditions.Add(conditionName)
                Catch ex2 As Exception
                    Trace.WriteLine("Incompatible Condition '" & conditionName & "' adding failed for Table'" & tableName & "'.")
                    Trace.WriteLine("Incompatible Condition exception: " & ex2.ToString)
                End Try
            End Try
        End Try
    End Sub
    'placeholder for extended COUNT functionality
    Private Function Universe_RemoveContexts(ByRef Univ As Designer.Universe) As String

        Dim Jn As Designer.Join
        Dim Cntxt As Designer.Context

        Dim extraJoin As UnivJoinsTPIde.UnivJoin

        'log extra joins

        'For Each Cntxt In Univ.Contexts
        'Try
        'For Each Jn In Univ.Contexts(Cntxt.Name).Joins
        'extraJoin = New UnivJoins.UnivJoin
        'extraJoin.Expression = Jn.Expression
        'extraJoin.Contexts = Cntxt.Name
        'extraJoin.Cardinality = setCardinality(Jn)
        'extra_joins.AddItem(extraJoin)
        'Next Jn
        'Catch ex As Exception
        'Trace.WriteLine("Context '" & Cntxt.Name & "' join marking failed. Error: " & ex.Message)
        'End Try
        'Next Cntxt


        'remove contexts
        For Each Cntxt In Univ.Contexts
            Cntxt.Delete()
        Next Cntxt

        'remove joins
        For Each Jn In Univ.Joins
            Jn.Delete()
        Next Jn

    End Function
    Function setCardinality(ByRef Jn As Designer.Join) As String
        If Jn.Cardinality = Designer.DsCardinality.dsManyToOneCardinality Then
            Return "n_to_1"
        End If
        If Jn.Cardinality = Designer.DsCardinality.dsOneToManyCardinality Then
            Return "1_to_n"
        End If
        If Jn.Cardinality = Designer.DsCardinality.dsOneToOneCardinality Then
            Return "1_to_1"
        End If
        If Jn.Cardinality = Designer.DsCardinality.dsManyToManyCardinality Then
            Return "n_to_n"
        End If
    End Function

    ''
    'Adds joins to universe and builds contexts.
    '@param     Univ        Specifies reference to universe
    '@param     mts         List of measurement types for current universe.
    '@param     univ_joins  List of joins read from dwhrep database.
    '@returns   joinsAdded  Boolean, true if adding the joins succeeded.
    '@remarks               Tables must be added before adding joins.
    Public Function Universe_AddJoins(ByRef Univ As Designer.IUniverse, ByRef mts As MeasurementTypesTPIde, _
                                       ByRef univ_joins As UnivJoinsTPIde, ByRef extraJoins As UnivJoinsTPIde) As Boolean
        Dim joinsAdded As Boolean = True
        Console.WriteLine("Adding joins to universe: " & Univ.LongName)
        Try
            ' Add the joins:
            addJoinsToUniverse(Univ, univ_joins, True)
            addJoinsToUniverse(Univ, extraJoins, False)

            ' Build the contexts (busy hour and ordinary contexts):
            If (rankBusyHourFunctionality = True) Then
                Dim universeProxy As IUniverseProxy = New UniverseProxy(Univ)
                universe_BuildRankBHContexts(universeProxy, mts, univ_joins)
            End If
            Universe_BuildContexts(Univ, mts, univ_joins)
        Catch ex As Exception
            joinsAdded = False
            Trace.WriteLine("Error adding joins: " & ex.ToString())
        End Try
        Return joinsAdded
    End Function

    ''
    'Goes through a list of joins and adds them to the universe.
    '@param Univ                Reference to universe
    '@param joins               List of joins read from dwhrep database.
    '@param addToUpdatedJoins   True if join expression should be added to "updatedJoins" string
    Public Sub addJoinsToUniverse(ByRef Univ As Designer.IUniverse, ByRef joins As UnivJoinsTPIde, ByVal addToUpdatedJoins As Boolean)
        ' The join in the universe:
        Dim designerJoin As Designer.IJoin
        ' Join read from dwhrep:
        Dim univJoin As UnivJoinsTPIde.UnivJoin
        Dim JoinCount As Integer
        Dim endTableInUniverse As Boolean = False

        For JoinCount = 1 To joins.Count
            Try
                univJoin = joins.Item(JoinCount)
                designerJoin = addJoinToUniverse(Univ, univJoin, addToUpdatedJoins)

                ' Set join cardinality and parse the join:
                Try
                    designerJoin.Cardinality = univJoin.Cardinality
                    If joinParse = True Then
                        designerJoin.Parse()
                    End If
                Catch ex As Exception
                    Trace.WriteLine("Error setting up join: " & ex.ToString)
                End Try
            Catch ex As Exception
                Trace.WriteLine("Error adding join: " & ex.ToString())
            End Try
        Next JoinCount
    End Sub

    ''
    'Adds a single join to the universe.
    '@param     Univ                Reference to universe
    '@param     univ_join           UnivJoinsTPIde.UnivJoin object
    '@param     addToUpdatedJoins   True if join expression should be added to "updatedJoins" string          
    '@returns   designerJoin        Reference to the join
    Public Function addJoinToUniverse(ByRef Univ As Designer.IUniverse, ByVal univ_join As UnivJoinsTPIde.UnivJoin, ByVal addToUpdatedJoins As Boolean) As Designer.IJoin
        Dim designerJoins As Designer.IJoins
        Dim designerJoin As Designer.IJoin

        Try
            designerJoins = getJoins(Univ)
            designerJoin = getJoin(designerJoins, univ_join.Expression)
            If (addToUpdatedJoins = True) Then
                UniverseFunctionsTPIde.updatedJoins &= designerJoin.Expression & ";"
            End If
        Catch ex As Exception
            Try
                ' If getting the join failed, add it to the universe:
                designerJoin = addJoin(designerJoins, univ_join.Expression)
                If (addToUpdatedJoins = True) Then
                    UniverseFunctionsTPIde.updatedJoins &= designerJoin.Expression & ";"
                End If
            Catch e As Exception
                Trace.WriteLine("Join Adding failed for '" & univ_join.Expression & "'.")
                Trace.WriteLine("Join Adding Exception: " & e.ToString)
            End Try
        End Try
        Return designerJoin
    End Function

    Protected Overridable Function getJoins(ByRef Univ As Designer.IUniverse) As Designer.IJoins
        Dim joins As Designer.IJoins
        joins = Univ.Joins
        Return joins
    End Function

    Protected Overridable Function getJoin(ByRef joins As Designer.IJoins, ByVal joinExpression As String) As Designer.IJoin
        Dim join As Designer.IJoin
        join = joins.Item(joinExpression)
        Return join
    End Function

    Protected Overridable Function addJoin(ByRef joins As Designer.IJoins, ByVal joinExpression As String) As Designer.IJoin
        Dim join As Designer.IJoin
        join = joins.Add(joinExpression)
        Return join
    End Function

    ''
    ' Adds busy hour contexts to the universe
    ' 
    ' @Param Univ Reference to the universe
    ' @Param mts The list of measurement types
    ' @Param univ_joins list of universe joins
    Public Overridable Sub universe_BuildRankBHContexts(ByVal universeProxy As IUniverseProxy, ByRef mts As MeasurementTypesTPIde, _
                                                        ByRef univ_joins As UnivJoinsTPIde)
        Trace.WriteLine("Adding Busy Hour contexts")

        Dim bo_contexts As New BOContextsTPIde
        Dim unvRankMt As MeasurementTypesTPIde.MeasurementType

        ' Get the rank tables:
        Dim rankMTs = tpUtilities.getRankMeasurementTypes(mts)
        Dim JoinCount As Integer

        Dim Count As Integer
        For Count = 0 To (rankMTs.Count - 1)
            unvRankMt = rankMTs.Item(Count)

            ' Only do check for rank mts that are not elembh:
            Dim contextName = unvRankMt.TypeName & "_RANKBH"
            Conxt = bo_contexts.addContext(universeProxy, contextName)

            For JoinCount = 1 To univ_joins.Count
                univ_join = univ_joins.Item(JoinCount)
                If InStrRev(univ_join.Expression, unvRankMt.TypeName + "_RANKBH") > 0 And univ_join.Contexts <> "ELEMBH" Then ' AndAlso univ_join.Contexts = "RANKBH" Then ' leaving this out to get all joins for RANKBH                    
                    AddJoinToContext(Conxt, univ_join)
                End If
            Next JoinCount
        Next

        For Count = 0 To (rankMTs.Count - 1)
            unvRankMt = rankMTs.Item(Count)

            ' Get context name:
            Dim contextName = unvRankMt.TypeName & "_RANKBH"
            Dim context1 As Designer.Context
            For Each context1 In universeProxy.getContexts()
                ' Go through all the contexts in the universe
                If (context1.Name = contextName) Then
                    Exit For
                End If
            Next

            ' Get the extra joins:
            Dim extraJoins As ArrayList
            extraJoins = univ_joins.RankBHJoins()
            Dim extraJoin As UnivJoinsTPIde.UnivJoin
            For JoinCount = 0 To (extraJoins.Count - 1)
                extraJoin = extraJoins.Item(JoinCount)
                If InStrRev(extraJoin.Expression, unvRankMt.TypeName + "_RANKBH.") > 0 Then 'This will add in the extra rank bh joins
                    AddJoinToContext(context1, extraJoin)
                End If
            Next
        Next
    End Sub

    ''
    ' Adds contexts to universe. 
    '
    ' @param Univ Specifies reference to universe
    Protected Overridable Sub Universe_BuildContexts(ByRef Univ As Designer.IUniverse, ByRef mts As MeasurementTypesTPIde, _
                                                     ByRef univ_joins As UnivJoinsTPIde)
        Dim JoinContexts() As String
        Dim ExcludedJoinContexts() As String
        Dim ContextCount As Short
        Dim ExcludedCount As Short
        Dim JnFound As Boolean
        Dim JoinCount As Integer

        Dim bo_contexts As New BOContextsTPIde
        Dim universeProxy As IUniverseProxy = New UniverseProxy(Univ)

        JnFound = False

        For mt_count = 1 To mts.Count

            mt = mts.Item(mt_count)

            If mt.RankTable = False Then
                If mt.PlainTable = True Then
                    Conxt = bo_contexts.addContext(universeProxy, mt.TypeName)
                Else
                    Conxt = bo_contexts.addContext(universeProxy, mt.TypeName + "_RAW")
                End If
                For JoinCount = 1 To univ_joins.Count
                    univ_join = univ_joins.Item(JoinCount)
                    If mt.PlainTable = True Then
                        If InStrRev(univ_join.Expression, mt.TypeName + ".") > 0 Then
                            AddJoinToContext(Conxt, univ_join)
                        End If
                    Else
                        If InStrRev(univ_join.Expression, mt.TypeName + "_RAW.") > 0 AndAlso InStrRev(univ_join.Expression, "ELEMBH_RANKBH.") = 0 AndAlso (univ_join.Contexts <> "RANKBH") Then
                            AddJoinToContext(Conxt, univ_join)
                        End If
                    End If
                Next JoinCount
            End If

            If mt.RankTable = False AndAlso mt.CreateCountTable = True Then
                Conxt = bo_contexts.addContext(universeProxy, mt.TypeName + "_COUNT")
                For JoinCount = 1 To univ_joins.Count
                    univ_join = univ_joins.Item(JoinCount)
                    If InStrRev(univ_join.Expression, mt.TypeName & "_COUNT.") > 0 AndAlso InStrRev(univ_join.Expression, "ELEMBH_RANKBH.") = 0 AndAlso (univ_join.Contexts <> "RANKBH") Then
                        AddJoinToContext(Conxt, univ_join)
                    End If
                Next JoinCount
                'placeholder for extended COUNT functionality
                If extendedCountObject = True Then
                    Conxt = bo_contexts.addContext(universeProxy, mt.TypeName + "_DELTA")
                    For JoinCount = 1 To univ_joins.Count
                        univ_join = univ_joins.Item(JoinCount)
                        If InStrRev(univ_join.Expression, mt.TypeName & "_DELTA.") > 0 AndAlso InStrRev(univ_join.Expression, "ELEMBH_RANKBH.") = 0 AndAlso (univ_join.Contexts <> "RANKBH") Then
                            AddJoinToContext(Conxt, univ_join)
                        End If
                    Next JoinCount
                End If
                'placeholder for extended COUNT functionality
            End If
            If mt.DayAggregation = True AndAlso mt.RankTable = False Then
                Conxt = bo_contexts.addContext(universeProxy, mt.TypeName + "_DAY")
                For JoinCount = 1 To univ_joins.Count
                    univ_join = univ_joins.Item(JoinCount)
                    If InStrRev(univ_join.Expression, mt.TypeName & "_DAY.") > 0 AndAlso (univ_join.Contexts <> "RANKBH") Then
                        AddJoinToContext(Conxt, univ_join)
                    End If
                Next JoinCount
            End If

            If mt.ObjectBusyHours <> "" AndAlso mt.RankTable = False Then
                Conxt = bo_contexts.addContext(universeProxy, mt.TypeName + "_DAYBH")
                For JoinCount = 1 To univ_joins.Count
                    univ_join = univ_joins.Item(JoinCount)
                    If InStrRev(univ_join.Expression, mt.TypeName & "_DAYBH.") > 0 AndAlso (univ_join.Contexts <> "RANKBH") Then
                        AddJoinToContext(Conxt, univ_join)
                    End If
                Next JoinCount
            End If

            If mt.ElementBusyHours = True AndAlso mt.RankTable = False Then
                Conxt = bo_contexts.addContext(universeProxy, mt.TypeName + "_ELEMBH")
                ' Add Element Joins
                For JoinCount = 1 To univ_joins.Count
                    univ_join = univ_joins.Item(JoinCount)
                    If mt.CreateCountTable = True Then
                        If InStrRev(univ_join.Expression, mt.TypeName & "_COUNT.") > 0 AndAlso InStrRev(univ_join.Expression, "ELEMBH_RANKBH.") > 0 AndAlso (univ_join.Contexts <> "RANKBH") Then
                            AddJoinToContext(Conxt, univ_join)
                        ElseIf InStrRev(univ_join.Expression, mt.TypeName & "_COUNT.") > 0 AndAlso univ_join.Contexts = "ELEMBH" AndAlso (univ_join.Contexts <> "RANKBH") Then
                            AddJoinToContext(Conxt, univ_join)
                        ElseIf InStrRev(univ_join.Expression, "_COUNT.") = 0 AndAlso InStrRev(univ_join.Expression, "_RAW.") = 0 AndAlso InStrRev(univ_join.Expression, "_DELTA.") = 0 AndAlso univ_join.Contexts = "ELEMBH" AndAlso (univ_join.Contexts <> "RANKBH") Then
                            AddJoinToContext(Conxt, univ_join)
                        Else
                            'do nothing
                        End If
                    Else
                        If InStrRev(univ_join.Expression, mt.TypeName & "_RAW.") > 0 AndAlso InStrRev(univ_join.Expression, "ELEMBH_RANKBH.") > 0 _
                        AndAlso (univ_join.Contexts <> "RANKBH") Then
                            AddJoinToContext(Conxt, univ_join)
                        ElseIf InStrRev(univ_join.Expression, mt.TypeName & "_RAW.") > 0 AndAlso univ_join.Contexts = "ELEMBH" _
                        AndAlso (univ_join.Contexts <> "RANKBH") Then
                            AddJoinToContext(Conxt, univ_join)
                        ElseIf InStrRev(univ_join.Expression, "_RAW.") = 0 AndAlso InStrRev(univ_join.Expression, "_COUNT.") = 0 AndAlso univ_join.Contexts = "ELEMBH" _
                        AndAlso (univ_join.Contexts <> "RANKBH") Then
                            AddJoinToContext(Conxt, univ_join)
                        Else
                            'do nothing
                        End If
                    End If

                Next JoinCount
            End If

            'placeholder for extended COUNT functionality
            If mt.ElementBusyHours = True AndAlso mt.RankTable = False AndAlso extendedCountObject = True AndAlso mt.CreateCountTable = True Then
                Conxt = bo_contexts.addContext(universeProxy, mt.TypeName + "_DELTA_ELEMBH")
                ' Add Element Joins
                For JoinCount = 1 To univ_joins.Count
                    univ_join = univ_joins.Item(JoinCount)
                    If InStrRev(univ_join.Expression, mt.TypeName & "_DELTA.") > 0 AndAlso InStrRev(univ_join.Expression, "ELEMBH_RANKBH.") > 0 _
                    AndAlso (univ_join.Contexts <> "RANKBH") Then
                        AddJoinToContext(Conxt, univ_join)
                    ElseIf InStrRev(univ_join.Expression, mt.TypeName & "_DELTA.") > 0 AndAlso univ_join.Contexts = "ELEMBH" _
                    AndAlso (univ_join.Contexts <> "RANKBH") Then
                        AddJoinToContext(Conxt, univ_join)
                    ElseIf InStrRev(univ_join.Expression, "_DELTA.") = 0 AndAlso InStrRev(univ_join.Expression, "_RAW.") = 0 AndAlso InStrRev(univ_join.Expression, "_COUNT.") = 0 AndAlso univ_join.Contexts = "ELEMBH" _
                    AndAlso (univ_join.Contexts <> "RANKBH") Then
                        AddJoinToContext(Conxt, univ_join)
                    Else
                        'do nothing
                    End If
                Next JoinCount
            End If
            'placeholder for extended COUNT functionality

        Next mt_count


        'TODO: placeholder for extended COUNT functionality
        Dim context_location As Integer
        For Each Conxt In Univ.Contexts
            For JoinCount = 1 To univ_joins.Count
                univ_join = univ_joins.Item(JoinCount)
                'all contexts
                If univ_join.Contexts = "All" AndAlso (univ_join.Contexts <> "RANKBH") Then ' don't do this for RANKBH tables
                    AddJoinToContext(Conxt, univ_join)
                ElseIf univ_join.Contexts <> "All" AndAlso univ_join.Contexts <> "" AndAlso univ_join.Contexts <> "ELEMBH" And univ_join.Contexts <> "RANKBH" Then
                    JoinContexts = Split(univ_join.Contexts, ",")

                    For ContextCount = 0 To UBound(JoinContexts)
                        context_location = InStrRev(Conxt.Name, JoinContexts(ContextCount) & "_")
                        If context_location > 0 Then
                            'If InStrRev(univ_join.Expression, Conxt.Name.Substring(0, context_location)) > 0 Then                            
                            If univ_join.ExcludedContexts <> "" Then
                                ExcludedJoinContexts = Split(univ_join.ExcludedContexts, ",")
                                For ExcludedCount = 0 To UBound(ExcludedJoinContexts)
                                    If InStrRev(Conxt.Name, ExcludedJoinContexts(ExcludedCount)) = 0 Then
                                        AddJoinToContext(Conxt, univ_join)
                                    End If
                                Next ExcludedCount
                            Else
                                AddJoinToContext(Conxt, univ_join)
                            End If
                            'End If
                        End If
                    Next ContextCount
                Else
                End If
            Next JoinCount
        Next Conxt

        'obsolete contexts
        'For Each Conxt In Univ.Contexts
        'For JoinCount = 1 To extra_joins.Count
        'univ_join = extra_joins.Item(JoinCount)
        'all contexts
        'If univ_join.Contexts <> "" Then
        'If univ_join.Contexts = Conxt.Name Then
        'AddJoinToContext(Conxt, univ_join)
        'End If
        'End If
        'Next JoinCount
        'Next Conxt

    End Sub

    ''
    ' Adds join to a context. 
    '
    ' @param Conxt Specifies reference to contexts
    ' @param univ_join Specifies reference to universe join
    Private Sub AddJoinToContext(ByRef Conxt As Designer.Context, ByRef univ_join As UnivJoinsTPIde.UnivJoin)
        ' Trace.WriteLine("Adding join to context: " & Conxt.Name & ", " & univ_join.Expression)
        Dim JnFound As Boolean
        JnFound = False
        Dim count As Integer

        If Not univ_join.ExcludedContexts <> "" AndAlso InStrRev(Conxt.Name, univ_join.ExcludedContexts) > 0
            Try
                For Each Jn In Conxt.Joins
                    If Jn.Expression = univ_join.Expression Then
                        JnFound = True
                        Exit For
                    End If
                Next
            Catch ex As Exception
                Trace.WriteLine("UniverseFunctionsTPIde, AddJoinToContext(): " _
                                & "Error checking joins in Context '" & Conxt.Name & "' join: " & univ_join.Expression & ". Error: " & ex.Message)
            End Try
            If JnFound = False Then
                Try
                    Jn = Conxt.Joins.Add(univ_join.Expression)
                Catch e As Exception
                    Trace.WriteLine("Adding join '" & univ_join.Expression & "' to context '" & Conxt.Name & "' fails. Check if joins exists for context already.")
                End Try
            End If
        End If
    End Sub
    ''
    ' Adds join to a context. 
    '
    ' @param Conxt Specifies reference to contexts
    ' @param univ_join Specifies reference to universe join
    Private Sub AddJoinToContext_New(ByRef Conxt As Designer.Context, ByRef univ_join As UnivJoinsTPIde.UnivJoin)
        Dim JnFound As Boolean
        JnFound = False
        Dim count As Integer
        If Not univ_join.ExcludedContexts <> "" AndAlso InStrRev(Conxt.Name, univ_join.ExcludedContexts) > 0 Then
            Try
                Jn = Conxt.Joins(univ_join.Expression)
            Catch ex As Exception
                Try
                    Jn = Conxt.Joins.Add(univ_join.Expression)
                Catch e As Exception
                    Trace.WriteLine("Adding join '" & univ_join.Expression & "' to context '" & Conxt.Name & "' fails. Check if joins exists for context already.")
                End Try
            End Try
        End If
    End Sub

    ''
    ' Adds additional object and conditions to universe.
    ' Objects are:
    ' - data_coverage
    ' - period_duration
    ' - datetime (raw)
    ' - hours from now
    ' Conditions are:
    ' - Latest N Hours
    '
    ' @param Univ Specifies reference to targetuniverse
    Function Universe_AddAdditionalObjectsAndConditions(ByRef Univ As Designer.Universe, ByRef mts As MeasurementTypesTPIde) As Boolean
        Dim Cls As Designer.Class
        Dim Obj As Designer.Object
        Dim Cond As Designer.PredefinedCondition

        Dim universeProxy As IUniverseProxy = New UniverseProxy(Univ)
        Dim bo_objects As New BOObjectsTPIde(universeProxy)
        Dim bo_conditions As New BOConditionsTPIde(universeProxy)
        Dim boLatestConditionsTPIde As New BOLatestConditionsTPIde()

        bo_objects.m_objectParse = additionalObjectParse
        bo_conditions.ConditionParse = additionalConditionParse
        Console.WriteLine("Adds additional object and conditions to universe")
        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            Try
            If mt.MeasurementTypeID <> "" AndAlso mt.RankTable = False Then
                If mt.DayAggregation = True AndAlso mt.CreateCountTable = False AndAlso mt.PlainTable = False Then
                    Cls = Univ.Classes.FindClass(mt.TypeName)
                    Obj = bo_objects.addObject(Cls, "data_coverage", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "data_coverage")
                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        If mt.DayAggregation = True Then
                            Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.DATACOVERAGE),sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_RAW.PERIOD_DURATION))"
                        Else
                            Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_RAW.PERIOD_DURATION))"
                        End If
                    Else
                        If mt.DayAggregation = True Then
                            Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.DATACOVERAGE),sum(DC." & mt.TypeName & "_RAW.PERIOD_DURATION))"
                        Else
                            Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_RAW.PERIOD_DURATION))"
                        End If
                    End If

                    Obj.Format.NumberFormat = "0;-0;0"
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    Obj = bo_objects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration")
                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        If mt.DayAggregation = True Then
                            Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.PERIOD_DURATION),sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_RAW.PERIOD_DURATION))"
                        Else
                            Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_RAW.PERIOD_DURATION))"
                        End If
                    Else
                        If mt.DayAggregation = True Then
                            Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.PERIOD_DURATION),sum(DC." & mt.TypeName & "_RAW.PERIOD_DURATION))"
                        Else
                            Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_RAW.PERIOD_DURATION))"
                        End If
                    End If

                    Obj.Format.NumberFormat = "0;-0;0"
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    Cls = Univ.Classes.FindClass(mt.TypeName & "_Keys")
                    ' Add MIN_ID object
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        Obj = bo_objects.addObject(Cls, "Min", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Minute for DAYBH")
                        Obj.Select = "DC." & mt.TypeName & "_DAYBH.MIN_ID"
                        ' Assign numeric format:
                        Obj.Format.NumberFormat = "0;-0;0"
                        If bo_objects.ParseObject(Obj, Cls) = False Then
                            Return False
                        End If
                    End If

                    'NE_offset
                    Obj = bo_objects.addObject(Cls, "NE_offset", Designer.DsObjectType.dsCharacterObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Network element time offset to UTC")
                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        If mt.DayAggregation = True Then
                            Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_TIMEZONE,DC." & mt.TypeName & "_DAYBH.DC_TIMEZONE,DC." & mt.TypeName & "_RAW.DC_TIMEZONE)"
                        Else
                            Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAYBH.DC_TIMEZONE,DC." & mt.TypeName & "_RAW.DC_TIMEZONE)"
                        End If

                    Else
                        If mt.DayAggregation = True Then
                            Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_TIMEZONE,DC." & mt.TypeName & "_RAW.DC_TIMEZONE)"
                        Else
                            Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_RAW.DC_TIMEZONE)"
                        End If
                    End If
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If


                    'NE_version
                    Obj = bo_objects.addObject(Cls, "NE Version", Designer.DsObjectType.dsCharacterObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "NE Version")
                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        If mt.DayAggregation = True Then
                            Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_RELEASE,DC." & mt.TypeName & "_DAYBH.DC_RELEASE,DC." & mt.TypeName & "_RAW.DC_RELEASE)"
                        Else
                            Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAYBH.DC_RELEASE,DC." & mt.TypeName & "_RAW.DC_RELEASE)"
                        End If

                    Else
                        If mt.DayAggregation = True Then
                            Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_RELEASE,DC." & mt.TypeName & "_RAW.DC_RELEASE)"
                        Else
                            Obj.Select = "DC." & mt.TypeName & "_RAW.DC_RELEASE"
                        End If
                    End If
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    'datetime (raw)
                    Obj = bo_objects.addObject(Cls, "datetime (raw)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "datetime (raw)")
                    Obj.Select = "DC." & mt.TypeName & "_RAW.DATETIME_ID"
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"
                    'hours from now
                    Obj = bo_objects.addObject(Cls, "hours from now", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "hours from now")
                    Obj.Select = "round(cast(datediff(minute,DC." & mt.TypeName & "_RAW.DATETIME_ID,now()) as real)/60,0)"
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    'TODO: UTC DATETIME OBJECT
                    Obj = bo_objects.addObject(Cls, "Datetime (UTC)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Datetime (UTC)")
                    Obj.Select = "DC." & mt.TypeName & "_RAW.UTC_DATETIME_ID"
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"
                    'TODO: UTC DATETIME CONDITION
                    Try
                        Cond = Cls.PredefinedConditions("UTC Datetime Between DT1 and DT2")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("UTC Datetime Between DT1 and DT2")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\Datetime (UTC))  BETWEEN @Prompt('First Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free) AND @Prompt('Last Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free)"
                    If bo_conditions.ParseCondition(Cond, Cls) = False Then
                        Return False
                    End If

                    Try
                        Cond = Cls.PredefinedConditions("Latest N Hours")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("Latest N Hours")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\hours from now) BETWEEN @Prompt('Excluded N Hours:','N',,,)+1 AND @Prompt('Latest N Hours:','N',,,) AND DC.DIM_DATE.DATE_ID BETWEEN DATEADD(hour,-@Prompt('Latest N Hours:','N',,,), now() ) AND DATEADD(hour,-(@Prompt('Excluded N Hours:','N',,,)+1), now() )"
                    If bo_conditions.ParseCondition(Cond, Cls) = False Then
                        Return False
                    End If

                    ' Add lookback conditions:
                    Trace.WriteLine("Adding lookback conditions in class: " & Cls.Name)
                    Dim addSuccessful As Boolean = True

                    ' Raw:
                    addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "RAW", "DATETIME_ID", bo_conditions, mt)

                    ' Day:
                    If mt.DayAggregation = True Then
                        addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "DAY", "DATE_ID", bo_conditions, mt)
                    End If

                    ' Day BH:
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "DAYBH", "DATE_ID", bo_conditions, mt)
                    End If

                    If (addSuccessful = False) Then
                        Trace.WriteLine("Error adding lookback period conditions to class: " & Cls.Name)
                    End If
                End If
            End If
            ' Add objects and conditions when Count is enabled:
            If mt.DayAggregation = True AndAlso mt.CreateCountTable = True Then
                Cls = Univ.Classes.FindClass(mt.TypeName)
                Obj = bo_objects.addObject(Cls, "data_coverage", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "data_coverage")

                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        'placeholder for extended COUNT functionality
                        If extendedCountObject = True Then
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.DATACOVERAGE),sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION),sum(DC." & mt.TypeName & "_DELTA.PERIOD_DURATION))"
                            Else
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION),sum(DC." & mt.TypeName & "_DELTA.PERIOD_DURATION))"
                            End If

                            'placeholder for extended COUNT functionality
                        Else
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.DATACOVERAGE),sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION))"
                            Else
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION))"
                            End If

                        End If
                    Else
                        'placeholder for extended COUNT functionality
                        If extendedCountObject = True Then
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.DATACOVERAGE),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION),sum(DC." & mt.TypeName & "_DELTA.PERIOD_DURATION))"
                            Else
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION),sum(DC." & mt.TypeName & "_DELTA.PERIOD_DURATION))"
                            End If

                            'placeholder for extended COUNT functionality
                        Else
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.DATACOVERAGE),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION))"
                            Else
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION))"
                            End If

                        End If
                    End If

                Obj.Format.NumberFormat = "0;-0;0"
                If bo_objects.ParseObject(Obj, Cls) = False Then
                    Return False
                End If
                Obj = bo_objects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration")

                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        'placeholder for extended COUNT functionality
                        If extendedCountObject = True Then
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.PERIOD_DURATION),sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION),sum(DC." & mt.TypeName & "_DELTA.PERIOD_DURATION))"
                            Else
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION),sum(DC." & mt.TypeName & "_DELTA.PERIOD_DURATION))"
                            End If

                            'placeholder for extended COUNT functionality
                        Else
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.PERIOD_DURATION),sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION))"
                            Else
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION))"
                            End If

                        End If
                    Else
                        'placeholder for extended COUNT functionality
                        If extendedCountObject = True Then
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.PERIOD_DURATION),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION),sum(DC." & mt.TypeName & "_DELTA.PERIOD_DURATION))"
                            Else
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION),sum(DC." & mt.TypeName & "_DELTA.PERIOD_DURATION))"
                            End If

                            'placeholder for extended COUNT functionality
                        Else
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.PERIOD_DURATION),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION))"
                            Else
                                Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION))"
                            End If

                        End If

                    End If
                Obj.Format.NumberFormat = "0;-0;0"
                If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    Try
                        Cls = Univ.Classes.FindClass(mt.TypeName & "_RAW")
                    Obj = bo_objects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration")
                        Obj.Select = "sum(DC." & mt.TypeName & "_RAW.PERIOD_DURATION)"
                    Obj.Format.NumberFormat = "0;-0;0"
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    Cls = Univ.Classes.FindClass(mt.TypeName & "_RAW_Keys")
                    Obj = bo_objects.addObject(Cls, "NE_offset", Designer.DsObjectType.dsCharacterObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Network element time offset to UTC")
                    Obj.Select = "DC." & mt.TypeName & "_RAW.DC_TIMEZONE"
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    Obj = bo_objects.addObject(Cls, "NE Version", Designer.DsObjectType.dsCharacterObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "NE Version")
                    Obj.Select = "DC." & mt.TypeName & "_RAW.DC_RELEASE"
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    Obj = bo_objects.addObject(Cls, "datetime (raw)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "datetime (raw)")
                    Obj.Select = "DC." & mt.TypeName & "_RAW.DATETIME_ID"
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"

                    Obj = bo_objects.addObject(Cls, "hours from now", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "hours from now")
                    Obj.Select = "round(cast(datediff(minute,DC." & mt.TypeName & "_RAW.DATETIME_ID,now()) as real)/60,0)"
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    'TODO: UTC DATETIME OBJECT
                    Obj = bo_objects.addObject(Cls, "Datetime (UTC)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Datetime (UTC)")
                    Obj.Select = "DC." & mt.TypeName & "_RAW.UTC_DATETIME_ID"
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                Catch ex As Exception
                    Trace.WriteLine("Universe_AddAdditionalObjectsAndConditions(): Error adding classes: " & ex.ToString())
                End Try

                'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"
                'TODO: UTC DATETIME CONDITION
                Try
                    Cond = Cls.PredefinedConditions("UTC Datetime Between DT1 and DT2")
                    UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                Catch e As Exception
                    Cond = Cls.PredefinedConditions.Add("UTC Datetime Between DT1 and DT2")
                    UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                End Try
                Cond.Description = ""
                Cond.Where = "@Select(" & Cls.Name & "\Datetime (UTC))  BETWEEN @Prompt('First Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free) AND @Prompt('Last Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free)"
                If bo_conditions.ParseCondition(Cond, Cls) = False Then
                    Return False
                End If

                Try
                    Cond = Cls.PredefinedConditions("Latest N Hours")
                    UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                Catch e As Exception
                    Cond = Cls.PredefinedConditions.Add("Latest N Hours")
                    UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                End Try
                Cond.Description = ""
                Cond.Where = "@Select(" & Cls.Name & "\hours from now) BETWEEN @Prompt('Excluded N Hours:','N',,,)+1 AND @Prompt('Latest N Hours:','N',,,) AND DC.DIM_DATE.DATE_ID BETWEEN DATEADD(hour,-@Prompt('Latest N Hours:','N',,,), now() ) AND DATEADD(hour,-(@Prompt('Excluded N Hours:','N',,,)+1), now() )"
                If bo_conditions.ParseCondition(Cond, Cls) = False Then
                    Return False
                End If

                ' Add new lookback condition for RAW (note that DAY, COUNT and DAYBH conditions are added in the _Keys class):
                Trace.WriteLine("Adding lookback condition in class: " & Cls.Name)
                ' RAW only:
                Dim addSuccessful As Boolean = True
                addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "RAW", "DATETIME_ID", bo_conditions, mt)

                If (addSuccessful = False) Then
                    Trace.WriteLine("Adding latest n hour (RAW) conditions to class: " & Cls.Name)
                End If

                Cls = Univ.Classes.FindClass(mt.TypeName & "_Keys")
                ' Add BHTYPE object
                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                    Obj = bo_objects.addObject(Cls, "Min", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Minute for DAYBH")
                    Obj.Select = "DC." & mt.TypeName & "_DAYBH.MIN_ID"
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                End If

                'NE_offset
                Obj = bo_objects.addObject(Cls, "NE_offset", Designer.DsObjectType.dsCharacterObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Network element time offset to UTC")
                    'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        'placeholder for extended COUNT functionality
                        If extendedCountObject = True Then
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_TIMEZONE,DC." & mt.TypeName & "_DAYBH.DC_TIMEZONE,DC." & mt.TypeName & "_COUNT.DC_TIMEZONE,DC." & mt.TypeName & "_DELTA.DC_TIMEZONE)"
                            Else
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAYBH.DC_TIMEZONE,DC." & mt.TypeName & "_COUNT.DC_TIMEZONE,DC." & mt.TypeName & "_DELTA.DC_TIMEZONE)"
                            End If

                            'placeholder for extended COUNT functionality
                        Else
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_TIMEZONE,DC." & mt.TypeName & "_DAYBH.DC_TIMEZONE,DC." & mt.TypeName & "_COUNT.DC_TIMEZONE)"
                            Else
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAYBH.DC_TIMEZONE,DC." & mt.TypeName & "_COUNT.DC_TIMEZONE)"
                            End If

                        End If
                    Else
                        'placeholder for extended COUNT functionality
                        If extendedCountObject = True Then
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_TIMEZONE,DC." & mt.TypeName & "_COUNT.DC_TIMEZONE,DC." & mt.TypeName & "_DELTA.DC_TIMEZONE)"
                            Else
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_COUNT.DC_TIMEZONE,DC." & mt.TypeName & "_DELTA.DC_TIMEZONE)"
                            End If

                            'placeholder for extended COUNT functionality
                        Else
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_TIMEZONE,DC." & mt.TypeName & "_COUNT.DC_TIMEZONE)"
                            Else
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_COUNT.DC_TIMEZONE)"
                            End If

                        End If
                    End If

                    'NE_Version
                Obj = bo_objects.addObject(Cls, "NE Version", Designer.DsObjectType.dsCharacterObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "NE Version")
                    'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        'placeholder for extended COUNT functionality
                        If extendedCountObject = True Then
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_RELEASE,DC." & mt.TypeName & "_DAYBH.DC_RELEASE,DC." & mt.TypeName & "_COUNT.DC_RELEASE,DC." & mt.TypeName & "_DELTA.DC_RELEASE)"
                            Else
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAYBH.DC_RELEASE,DC." & mt.TypeName & "_COUNT.DC_RELEASE,DC." & mt.TypeName & "_DELTA.DC_RELEASE)"
                            End If

                            'placeholder for extended COUNT functionality
                        Else
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_RELEASE,DC." & mt.TypeName & "_DAYBH.DC_RELEASE,DC." & mt.TypeName & "_COUNT.DC_RELEASE)"
                            Else
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAYBH.DC_RELEASE,DC." & mt.TypeName & "_COUNT.DC_RELEASE)"
                            End If

                        End If
                    Else
                        'placeholder for extended COUNT functionality
                        If extendedCountObject = True Then
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_RELEASE,DC." & mt.TypeName & "_COUNT.DC_RELEASE,DC." & mt.TypeName & "_DELTA.DC_RELEASE)"
                            Else
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_COUNT.DC_RELEASE,DC." & mt.TypeName & "_DELTA.DC_RELEASE)"
                            End If

                            'placeholder for extended COUNT functionality
                        Else
                            If mt.DayAggregation = True Then
                                Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_RELEASE,DC." & mt.TypeName & "_COUNT.DC_RELEASE)"
                            Else
                                Obj.Select = "DC." & mt.TypeName & "_COUNT.DC_RELEASE"
                            End If

                        End If
                    End If


                Obj = bo_objects.addObject(Cls, "datetime (raw)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "datetime (raw)")
                    'placeholder for extended COUNT functionality
                    If extendedCountObject = True Then
                        Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_COUNT.DATETIME_ID,DC." & mt.TypeName & "_DELTA.DATETIME_ID)"
                        'placeholder for extended COUNT functionality
                    Else
                        Obj.Select = "DC." & mt.TypeName & "_COUNT.DATETIME_ID"
                    End If
                If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"

                Obj = bo_objects.addObject(Cls, "hours from now", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "hours from now")
                    If extendedCountObject = True Then
                        Obj.Select = "@aggregate_aware(round(cast(datediff(minute,DC." & mt.TypeName & "_COUNT.DATETIME_ID,now()) as real)/60,0),round(cast(datediff(minute,DC." & mt.TypeName & "_DELTA.DATETIME_ID,now()) as real)/60,0))"
                    Else
                        Obj.Select = "round(cast(datediff(minute,DC." & mt.TypeName & "_COUNT.DATETIME_ID,now()) as real)/60,0)"
                    End If
                If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    'TODO: UTC DATETIME OBJECT
                Obj = bo_objects.addObject(Cls, "Datetime (UTC)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Datetime (UTC)")
                    If extendedCountObject = True Then
                        Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_COUNT.UTC_DATETIME_ID,DC." & mt.TypeName & "_DELTA.UTC_DATETIME_ID)"
                    Else
                        Obj.Select = "DC." & mt.TypeName & "_COUNT.UTC_DATETIME_ID"
                    End If

                If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"
                    'TODO: UTC DATETIME CONDITION
                    Try
                        Cond = Cls.PredefinedConditions("UTC Datetime Between DT1 and DT2")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("UTC Datetime Between DT1 and DT2")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\Datetime (UTC))  BETWEEN @Prompt('First Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free) AND @Prompt('Last Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free)"
                If bo_conditions.ParseCondition(Cond, Cls) = False Then
                        Return False
                    End If

                    Try
                        Cond = Cls.PredefinedConditions("Latest N Hours")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("Latest N Hours")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\hours from now) BETWEEN @Prompt('Excluded N Hours:','N',,,)+1 AND @Prompt('Latest N Hours:','N',,,) AND DC.DIM_DATE.DATE_ID BETWEEN DATEADD(hour,-@Prompt('Latest N Hours:','N',,,), now() ) AND DATEADD(hour,-(@Prompt('Excluded N Hours:','N',,,)+1), now() )"
                If bo_conditions.ParseCondition(Cond, Cls) = False Then
                        Return False
                    End If

                ' Add new conditions for lookback period:
                addSuccessful = True
                Trace.WriteLine("Adding lookback conditions in class: " & Cls.Name)

                ' Count:
                addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "COUNT", "DATETIME_ID", bo_conditions, mt)
                ' Delta:
                addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "DELTA", "DATETIME_ID", bo_conditions, mt)
                ' Day:
                If mt.DayAggregation = True Then
                    addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "DAY", "DATE_ID", bo_conditions, mt)
                End If

                If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                    ' Day BH:
                    addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "DAYBH", "DATE_ID", bo_conditions, mt)
                End If

                If (addSuccessful = False) Then
                    Trace.WriteLine("Error adding Select lookback conditions to class: " & Cls.Name)
                End If
                If FullAware = False Then
                    If mt.ObjectBusyHours <> "" Then
                        Cls = Univ.Classes.FindClass(mt.TypeName & "_BH")
                        Obj = bo_objects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration")
                        Obj.Select = "sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION)"
                        If bo_objects.ParseObject(Obj, Cls) = False Then
                            Return False
                        End If
                    End If
                End If
                If mt.PlainTable = True Then
                    Cls = Univ.Classes.FindClass(mt.TypeName)
                    Obj = bo_objects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration")
                    Obj.Select = "sum(DC." & mt.TypeName & ".PERIOD_DURATION)"
                    Obj.Format.NumberFormat = "0;-0;0"
                    If bo_objects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                End If
                End If
            Catch ex As Exception
                Trace.WriteLine("Error adding additional objects and conditions: " & ex.ToString())
            End Try
        Next mt_count

        'placeholder for extended COUNT functionality
        If extendedCountObject = True Then
            Try
                Cls = Univ.Classes.FindClass("General")
                Cond = Cls.PredefinedConditions("Immediate Delta Calculation")
                UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
            Catch e As Exception
                Cond = Cls.PredefinedConditions.Add("Immediate Delta Calculation")
                UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
            End Try
            Cond.Description = "Perform immediate Delta Calculation using DELTA views"
            Cond.Where = "0=0"
            If bo_conditions.ParseCondition(Cond, Cls) = False Then
                Return False
            End If
        End If
        'placeholder for extended COUNT functionality
        Return True
    End Function

    ''
    ' Adds additional object and conditions to universe.
    ' Objects are:
    ' - data_coverage
    ' - period_duration
    ' - datetime (raw)
    ' - hours from now
    ' Conditions are:
    ' - Latest N Hours
    '
    ' @param Univ Specifies reference to targetuniverse
    Function Universe_AddAdditionalComputedObjectsAndConditions(ByRef Univ As Designer.Universe, ByRef mts As MeasurementTypesTPIde) As Boolean
        Dim Cls As Designer.Class
        Dim Obj As Designer.Object
        Dim Cond As Designer.PredefinedCondition
        Dim bo_computedobjects As New BOComputedObjectTPIde
        Dim universeProxy As IUniverseProxy = New UniverseProxy(Univ)
        Dim bo_conditions As New BOConditionsTPIde(universeProxy)
        Dim boLatestConditionsTPIde As New BOLatestConditionsTPIde            
        bo_computedobjects.ObjectParse = additionalObjectParse
        bo_conditions.ConditionParse = additionalConditionParse

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" AndAlso mt.RankTable = False Then
                If mt.DayAggregation = True AndAlso mt.CreateCountTable = False AndAlso mt.PlainTable = False Then
                    Cls = Univ.Classes.FindClass(mt.TypeName)
                    Obj = bo_computedobjects.addObject(Cls, "data_coverage", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "data_coverage")

                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.DATACOVERAGE),sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_RAW.PERIOD_DURATION))"
                    Else
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.DATACOVERAGE),sum(DC." & mt.TypeName & "_RAW.PERIOD_DURATION))"
                    End If

                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    Obj = bo_computedobjects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration")

                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.PERIOD_DURATION),sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_RAW.PERIOD_DURATION))"
                    Else
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.PERIOD_DURATION),sum(DC." & mt.TypeName & "_RAW.PERIOD_DURATION))"
                    End If

                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    Cls = Univ.Classes.FindClass(mt.TypeName & "_Keys")
                    'NE_offset
                    Obj = bo_computedobjects.addObject(Cls, "NE_offset", Designer.DsObjectType.dsCharacterObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Network element time offset to UTC")
                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_TIMEZONE,DC." & mt.TypeName & "_DAYBH.DC_TIMEZONE,DC." & mt.TypeName & "_RAW.DC_TIMEZONE)"
                    Else
                        Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_TIMEZONE,DC." & mt.TypeName & "_RAW.DC_TIMEZONE)"
                    End If

                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    Obj = bo_computedobjects.addObject(Cls, "datetime (raw)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "datetime (raw)")
                    Obj.Select = "DC." & mt.TypeName & "_RAW.DATETIME_ID"
                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"
                    Obj = bo_computedobjects.addObject(Cls, "hours from now", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "hours from now")
                    Obj.Select = "round(cast(datediff(minute,DC." & mt.TypeName & "_RAW.DATETIME_ID,now()) as real)/60,0)"
                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    'TODO: UTC DATETIME OBJECT
                    Obj = bo_computedobjects.addObject(Cls, "Datetime (UTC)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Datetime (UTC)")
                    Obj.Select = "DC." & mt.TypeName & "_RAW.UTC_DATETIME_ID"
                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"
                    'TODO: UTC DATETIME CONDITION
                    Try
                        Cond = Cls.PredefinedConditions("UTC Datetime Between DT1 and DT2")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("UTC Datetime Between DT1 and DT2")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\Datetime (UTC))  BETWEEN @Prompt('First Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free) AND @Prompt('Last Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free)"
                    If bo_conditions.ParseCondition(Cond, Cls) = False Then
                        Return False
                    End If


                    Try
                        Cond = Cls.PredefinedConditions("Latest N Hours")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("Latest N Hours")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\hours from now) BETWEEN @Prompt('Excluded N Hours:','N',,,)+1 AND @Prompt('Latest N Hours:','N',,,) AND DC.DIM_DATE.DATE_ID BETWEEN DATEADD(hour,-@Prompt('Latest N Hours:','N',,,), now() ) AND DATEADD(hour,-(@Prompt('Excluded N Hours:','N',,,)+1), now() )"
                    If bo_conditions.ParseCondition(Cond, Cls) = False Then
                        Return False
                    End If

                    ' Add lookback conditions:
                    Trace.WriteLine("Adding lookback conditions in class: " & Cls.Name)
                    Dim addSuccessful As Boolean = True

                    ' Raw:
                    addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "RAW", "DATETIME_ID", bo_conditions, mt)

                    ' Day:
                    If mt.DayAggregation = True Then
                        addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "DAY", "DATE_ID", bo_conditions, mt)
                    End If

                    ' Day BH:
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "DAYBH", "DATE_ID", bo_conditions, mt)
                    End If

                    If (addSuccessful = False) Then
                        Trace.WriteLine("Error adding lookback period conditions to class: " & Cls.Name)
                    End If
                End If
                If mt.DayAggregation = True AndAlso mt.CreateCountTable = True Then
                    Cls = Univ.Classes.FindClass(mt.TypeName)
                    Obj = bo_computedobjects.addObject(Cls, "data_coverage", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "data_coverage")

                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.DATACOVERAGE),sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION))"
                    Else
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.DATACOVERAGE),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION))"
                    End If

                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    Obj = bo_computedobjects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration")

                    'If contains object busy hours, aggregate aware over DAY,DAYBH,RAW; else over DAY,RAW
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.PERIOD_DURATION),sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION))"
                    Else
                        Obj.Select = "@aggregate_aware(sum(DC." & mt.TypeName & "_DAY.PERIOD_DURATION),sum(DC." & mt.TypeName & "_COUNT.PERIOD_DURATION))"
                    End If

                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    Cls = Univ.Classes.FindClass(mt.TypeName & "_RAW")
                    Obj = bo_computedobjects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration")
                    Obj.Select = "sum(DC." & mt.TypeName & "_RAW.PERIOD_DURATION)"
                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    Cls = Univ.Classes.FindClass(mt.TypeName & "_RAW_Keys")
                    Obj = bo_computedobjects.addObject(Cls, "NE_offset", Designer.DsObjectType.dsCharacterObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Network element time offset to UTC")
                    Obj.Select = "DC." & mt.TypeName & "_RAW.DC_TIMEZONE"
                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    Obj = bo_computedobjects.addObject(Cls, "datetime (raw)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "datetime (raw)")
                    Obj.Select = "DC." & mt.TypeName & "_RAW.DATETIME_ID"
                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"

                    Obj = bo_computedobjects.addObject(Cls, "hours from now", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "hours from now")
                    Obj.Select = "round(cast(datediff(minute,DC." & mt.TypeName & "_RAW.DATETIME_ID,now()) as real)/60,0)"
                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    'TODO: UTC DATETIME OBJECT
                    Obj = bo_computedobjects.addObject(Cls, "Datetime (UTC)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Datetime (UTC)")
                    Obj.Select = "DC." & mt.TypeName & "_RAW.UTC_DATETIME_ID"
                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"
                    'TODO: UTC DATETIME CONDITION
                    Try
                        Cond = Cls.PredefinedConditions("UTC Datetime Between DT1 and DT2")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("UTC Datetime Between DT1 and DT2")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\Datetime (UTC))  BETWEEN @Prompt('First Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free) AND @Prompt('Last Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free)"
                    If bo_conditions.ParseCondition(Cond, Cls) = False Then
                        Return False
                    End If

                    Try
                        Cond = Cls.PredefinedConditions("Latest N Hours")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("Latest N Hours")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\hours from now) BETWEEN @Prompt('Excluded N Hours:','N',,,)+1 AND @Prompt('Latest N Hours:','N',,,) AND DC.DIM_DATE.DATE_ID BETWEEN DATEADD(hour,-@Prompt('Latest N Hours:','N',,,), now() ) AND DATEADD(hour,-(@Prompt('Excluded N Hours:','N',,,)+1), now() )"
                    If bo_conditions.ParseCondition(Cond, Cls) = False Then
                        Return False
                    End If

                    Dim addSuccessful As Boolean = True
                    Trace.WriteLine("Adding lookback conditions in class: " & Cls.Name)
                    ' Add RAW only:
                    addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "RAW", "DATETIME_ID", bo_conditions, mt)

                    If (addSuccessful = False) Then
                        Trace.WriteLine("Error adding Select lookback conditions to class: " & Cls.Name)
                    End If

                    Cls = Univ.Classes.FindClass(mt.TypeName & "_Keys")
                    'NE_offset
                    Obj = bo_computedobjects.addObject(Cls, "NE_offset", Designer.DsObjectType.dsCharacterObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Network element time offset to UTC")
                    'If contains object busy hours, aggregate aware over DAY,DAYBH,COUNT; else over DAY,COUNT
                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_TIMEZONE,DC." & mt.TypeName & "_DAYBH.DC_TIMEZONE,DC." & mt.TypeName & "_COUNT.DC_TIMEZONE)"
                    Else
                        Obj.Select = "@aggregate_aware(DC." & mt.TypeName & "_DAY.DC_TIMEZONE,DC." & mt.TypeName & "_COUNT.DC_TIMEZONE)"
                    End If
                    Obj = bo_computedobjects.addObject(Cls, "datetime (raw)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "datetime (raw)")
                    Obj.Select = "DC." & mt.TypeName & "_COUNT.DATETIME_ID"
                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"

                    Obj = bo_computedobjects.addObject(Cls, "hours from now", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "hours from now")
                    Obj.Select = "round(cast(datediff(minute,DC." & mt.TypeName & "_COUNT.DATETIME_ID,now()) as real)/60,0)"
                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If

                    'TODO: UTC DATETIME OBJECT
                    Obj = bo_computedobjects.addObject(Cls, "Datetime (UTC)", Designer.DsObjectType.dsDateObject, Designer.DsObjectQualification.dsDimensionObject, Designer.DsObjectAggregate.dsAggregateByNullObject, "Datetime (UTC)")
                    Obj.Select = "DC." & mt.TypeName & "_COUNT.UTC_DATETIME_ID"
                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                    'Obj.DataBaseFormat = "'yyyy-mm-dd HH:mm:ss'"
                    'TODO: UTC DATETIME CONDITION
                    Try
                        Cond = Cls.PredefinedConditions("UTC Datetime Between DT1 and DT2")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("UTC Datetime Between DT1 and DT2")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\Datetime (UTC))  BETWEEN @Prompt('First Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free) AND @Prompt('Last Datetime (UTC):','D','" & Cls.Name & "\Datetime (UTC)',mono,free)"
                    If bo_conditions.ParseCondition(Cond, Cls) = False Then
                        Return False
                    End If

                    Try
                        Cond = Cls.PredefinedConditions("Latest N Hours")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    Catch e As Exception
                        Cond = Cls.PredefinedConditions.Add("Latest N Hours")
                        UniverseFunctionsTPIde.updatedConditions &= Cls.Name & "/" & Cond.Name & ";"
                    End Try
                    Cond.Description = ""
                    Cond.Where = "@Select(" & Cls.Name & "\hours from now) BETWEEN @Prompt('Excluded N Hours:','N',,,)+1 AND @Prompt('Latest N Hours:','N',,,) AND DC.DIM_DATE.DATE_ID BETWEEN DATEADD(hour,-@Prompt('Latest N Hours:','N',,,), now() ) AND DATEADD(hour,-(@Prompt('Excluded N Hours:','N',,,)+1), now() )"
                    If bo_conditions.ParseCondition(Cond, Cls) = False Then
                        Return False
                    End If

                    ' Add new conditions for lookback period:
                    addSuccessful = True
                    Trace.WriteLine("Adding lookback conditions in class: " & Cls.Name)
                    ' Count:
                    addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "COUNT", "DATETIME_ID", bo_conditions, mt)
                    ' Delta:
                    addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "DELTA", "DATETIME_ID", bo_conditions, mt)
                    ' Day:
                    If mt.DayAggregation = True Then
                        addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "DAY", "DATE_ID", bo_conditions, mt)
                    End If

                    If mt.ObjectBusyHours <> "" AndAlso FullAware = True Then
                        ' Day BH:
                        addSuccessful = boLatestConditionsTPIde.addLatestNHoursOrDaysCondition(universeProxy, Cls, "DAYBH", "DATE_ID", bo_conditions, mt)
                    End If

                    If (addSuccessful = False) Then
                        Trace.WriteLine("Error adding Select lookback conditions to class: " & Cls.Name)
                    End If
                End If
                If FullAware = False Then
                    If mt.ObjectBusyHours <> "" Then
                        Cls = Univ.Classes.FindClass(mt.TypeName & "_BH")
                        Obj = bo_computedobjects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration")
                        Obj.Select = "sum(DC." & mt.TypeName & "_DAYBH.PERIOD_DURATION)"
                        If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                            Return False
                        End If
                    End If
                End If
                If mt.PlainTable = True Then
                    Cls = Univ.Classes.FindClass(mt.TypeName)
                    Obj = bo_computedobjects.addObject(Cls, "period_duration", Designer.DsObjectType.dsNumericObject, Designer.DsObjectQualification.dsMeasureObject, Designer.DsObjectAggregate.dsAggregateBySumObject, "period_duration")
                    Obj.Select = "sum(DC." & mt.TypeName & ".PERIOD_DURATION)"
                    If bo_computedobjects.ParseObject(Obj, Cls) = False Then
                        Return False
                    End If
                End If
            End If
        Next mt_count
        Return True
    End Function

    ''
    'Updates universe.
    '@param boUser          The user name for logging on to Business Objects server.
    '@param boPass          Password for Business Objects server.
    '@param boRep           The name of the BO server, in the format <server name>:<port number>
    '@param Filename        Specifies TP definition's filename.
    '@param CMTechPack      Specifies tech pack type. Value is True if tech tech pack is CM. Value is False if tech tech pack is PM.
    '@param EBSTechPack     Specifies tech pack update type. Value is True if tech tech pack is EBS else value is false
    '@param BaseFilename    Specifies base definition's filename.
    '@param OutputDir_Original The output directory where the updated universe will be saved.
    '@param EniqEnvironment The ODBC connection name.
    '@param BoVersion       BO version, either 6.5 or XI.
    '@param BoAut           Authentication level for BO.
    '@returns               Returns log messages once the universe update is finished.
    Function UpdateUniverse(ByRef boUser As String, ByRef boPass As String, ByRef boRep As String, ByRef Filename As String, _
                            ByRef CMTechPack As Boolean, ByRef EBSTechPack As Boolean, ByRef BaseFilename As String, _
                            ByRef OutputDir_Original As String, ByRef EniqEnvironment As String, ByRef BoVersion As String, _
                            ByRef BoAut As String) As Boolean
        Dim classNameAndFunction As String
        classNameAndFunction = className & "," & GetCurrentMethod.Name & ": "
        ' Dim DesignerApp As Designer.Application

        Dim retry As Boolean
        Dim ClsInit As Integer
        Dim Univ As Designer.Universe
        Dim Result As MsgBoxResult
        Dim checkItems As Designer.CheckedItems
        Dim checkItem As Designer.CheckedItem
        Dim OutputDir As String
        Dim count As Integer
        FullAware = True

        'zero update information
        updatedTables = ""
        updatedClasses = ""
        updatedObjects = ""
        updatedConditions = ""
        updatedJoins = ""
        updatedContexts = ""
        extra_joins = New UnivJoinsTPIde

        'update build number
        'Dim tp_excel = New TPExcelWriter
        'Dim updateBuild = tp_excel.updateBuildNumber(Filename, "universe", OutputDir_Original)
        'tp_excel = Nothing

        TechPackTPIde = Filename
        BaseTechPackTPIde = BaseFilename

        ' Set up the ODBC connection:
        Try
            tpAdoConn = "DSN=" & EniqEnvironment & ";"
            baseAdoConn = "DSN=" & EniqEnvironment & ";"

            tpConn = New System.Data.Odbc.OdbcConnection()
            tpConn.ConnectionString = tpAdoConn
            baseConn = New System.Data.Odbc.OdbcConnection()
            baseConn.ConnectionString = baseAdoConn
        Catch ex As Exception
            Trace.WriteLine("ODBC Exception: " + ex.ToString)
            Return False
        End Try

        Try
            tpConn.Open()
            baseConn.Open()
        Catch ex As Exception
            'modified for TR66103-Starts
            Console.WriteLine("Please check the ODBC connection : The Database server was not found ")
            'modified for TR66103-Ends
            Trace.WriteLine("ODBC Exception: " + ex.ToString)
            Return False
        End Try

        Try
            DesignerApp = tpUtilities.setupDesignerApp(BoVersion, boUser, boPass, boRep, BoAut)
        Catch ex As Exception
            Console.WriteLine("Error setting up designer application, exiting.")
            Trace.WriteLine(classNameAndFunction & "Error setting up designer application, exiting.")
            Return False
        End Try

        ClsInit = 0
        ClsInit = Initialize_Classes(OutputDir_Original)
        UpdateVersionProperties(OutputDir_Original)

        Try
        If ClsInit = 1 Then
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
        Else
            For count = 1 To UnvMts.Count
                UnvMt = UnvMts.Item(count)
                    Dim upgradeSuccessful As Boolean = upgradeUniverse(DesignerApp, UnvMt.MeasurementTypes, _
                UnvMt.ReferenceTypes, UnvMt.VectorReferenceTypes, _
                UnvMt.UnivJoins, UnvMt.ReferenceDatas, UnvMt.VectorReferenceDatas, _
                    OutputDir_Original, BoVersion, UnvMt.UniverseNameExtension, UnvMt.UniverseExtension, CMTechPack, EBSTechPack)

                    If (upgradeSuccessful = False) Then
                        Throw New Exception("Error upgrading universe: " & TechPackTPIde & " " & UnvMt.UniverseExtension)
                    End If
                Next count
            End If
        Catch ex As Exception
            Trace.WriteLine("Error upgrading universe: " & ex.ToString())
            Console.WriteLine("Error upgrading universe: " & ex.ToString())
            tpUtilities.displayMessageBox("Universe upgrade failed", MsgBoxStyle.Critical, "Upgrade failed")
        Catch nullException As System.NullReferenceException
            Trace.WriteLine("Error upgrading universe: " & nullException.ToString())
            Console.WriteLine("Null exception while upgrading universe: " & nullException.ToString())
            tpUtilities.displayMessageBox("Universe upgrade failed", MsgBoxStyle.Critical, "Upgrade failed")
        End Try

        tpConn.Close()
        baseConn.Close()
        DesignerApp.Visible = True
        DesignerApp.Interactive = True
        DesignerApp.Quit()

        Return True

    End Function

    ''
    'Closes database connections for tech packs, quits designer application.
    '@returns True if 
    Public Function cleanup() As Boolean
        ' Check the main tech pack database connection:
        If (Not tpConn Is Nothing AndAlso tpConn.State <> System.Data.ConnectionState.Closed) Then
            tpConn.Close()
            Trace.WriteLine("Closed tech pack database connection")
        End If
        ' Check the base tech pack database connection:
        If (Not baseConn Is Nothing AndAlso baseConn.State <> System.Data.ConnectionState.Closed) Then
            baseConn.Close()
            Trace.WriteLine("Closed base tech pack database connection")
        End If
        ' Check designer application:
        If (Not DesignerApp Is Nothing) Then
            Try
                DesignerApp.Visible = True
                DesignerApp.Interactive = True
                DesignerApp.Quit()
                Trace.WriteLine("Exited Designer application")
            Catch ex As Exception
                Trace.WriteLine("Error exiting designer application " & ex.ToString())
            End Try
        End If

        ' Check BO application:
        'If (Not BoApp Is Nothing) Then
        '    Try
        '        BoApp.Quit()
        '        Trace.WriteLine("Exited BO application (used for report generation)")
        '    Catch ex As Exception
        '        Trace.WriteLine("Error exiting BO application" & ex.ToString())
        '    End Try
        'End If

    End Function

    Public Overridable Function upgradeUniverse(ByVal DesignerApp As Designer.Application, ByRef mts As MeasurementTypesTPIde, ByRef rts As ReferenceTypesTPIde, _
    ByRef vector_rts As ReferenceTypesTPIde, ByRef univ_joins As UnivJoinsTPIde, ByRef rds As ReferenceDatasTPIde, ByRef vector_rds As ReferenceDatasTPIde, _
    ByRef OutputDir_Original As String, ByRef BoVersion As String, ByRef UniverseExtension As String, ByRef UniverseNameExtension As String, _
    ByRef CMTechPack As Boolean, ByRef EBSTechPack As Boolean) As Boolean

        Dim classNameAndFunction = className & "," & GetCurrentMethod.Name & ": "
        Dim retry As Boolean
        Dim ClsInit As Integer
        Dim Univ As Designer.Universe
        Dim OutputDir As String

        'zero update information
        updatedTables = ""
        updatedClasses = ""
        updatedObjects = ""
        updatedConditions = ""
        updatedJoins = ""
        updatedContexts = ""
        extra_joins = New UnivJoinsTPIde

        FullAware = True

        Dim SaveFileName = ""

        Try
            ' Open universe:
            Univ = tpUtilities.promptToOpenUniverse(UniverseNameExtension, UniverseExtension, BoVersion, DesignerApp, UniverseName, UniverseFileName, _
                                                    OutputDir_Original)
        Catch ex As Exception
            Trace.WriteLine(classNameAndFunction & "Error opening universe, exiting: " & ex.ToString())
            tpUtilities.displayMessageBox("Error opening universe for upgrade", MsgBoxStyle.Exclamation, "Error opening universe")
            Return False
        End Try
        SaveFileName = getUniverseFilename(UniverseFileName, UniverseExtension, UniverseName, UniverseNameExtension, BoVersion)
        System.Threading.Thread.Sleep(5000) ' Sleep for 5 seconds

        ObjectBHSupport = getObjectBHSupport(mts)
        ElementBHSupport = getElementBHSupport(mts)

        If UniverseNameExtension <> "" Then
            SetParameter(Univ, "Name", UniverseName & " " & UniverseNameExtension)
        Else
            SetParameter(Univ, "Name", UniverseName)
        End If
        SetParameter(Univ, "Description", UniverseDescription)

        Try
            SetParameter(Univ, "Connection", DesignerApp.Connections(1).Name)
        Catch ex As Exception
            Trace.WriteLine("Universe Connection Error: " & ex.Message & ".")
        End Try

        If Universe_AddTables(Univ, ObjectBHSupport, ElementBHSupport, False, mts, rts, vector_rts, UniverseExtension) = False Then
            Trace.WriteLine("Universe upgrade stopped while adding tables.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Exit Function
        End If

        'HQ15753
        Trace.WriteLine("Commented out the function Universe_RemoveContexts")
        Console.WriteLine("Commented out the function Universe_RemoveContexts")
        'Universe_RemoveContexts(Univ)

        If Universe_AddJoins(Univ, mts, univ_joins, extra_joins) = False Then
            Trace.WriteLine("Universe upgrade stopped while adding joins.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Exit Function
        End If


        'Univ.RefreshStructure()
        'Univ.ArrangeTables()

        If Universe_AddClasses(Univ, ObjectBHSupport, ElementBHSupport, False, UniverseExtension) = False Then
            Trace.WriteLine("Universe upgrade stopped while adding classes.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If


        If Universe_AddCounters(Univ, CMTechPack, EBSTechPack, mts, vector_rds) = False Then
            Trace.WriteLine("Universe upgrade stopped while adding counters.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If

        ' If Universe_AddComputedCounters(Univ, CMTechPack, EBSTechPack, mts, vector_rds) = False Then
        'Trace.WriteLine("Universe upgrade stopped while adding computed counters.")
        'tpConn.Close()
        'baseConn.Close()
        'DesignerApp.Quit()
        'Return logs
        'End If

        If Universe_AddCounterKeys(Univ, mts) = False Then
            Trace.WriteLine("Universe upgrade stopped while adding counter keys.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If

        'If Universe_AddComputedCounterKeys(Univ, mts) = False Then
        'Trace.WriteLine("Universe upgrade stopped while adding computed counter keys.")
        'tpConn.Close()
        'baseConn.Close()
        'DesignerApp.Quit()
        'Return logs
        'End If

        If Universe_AddAdditionalObjectsAndConditions(Univ, mts) = False Then
            Trace.WriteLine("Universe upgrade stopped while adding additional objects and conditions. Please, Contact Support.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If

        'If Universe_AddAdditionalComputedObjectsAndConditions(Univ, mts) = False Then
        'Trace.WriteLine("Universe upgrade stopped while adding additional computed objects and conditions. Please, Contact Support.")
        'tpConn.Close()
        'baseConn.Close()
        'DesignerApp.Quit()
        'Return logs
        'End If

        If Universe_AddObjects(Univ, ObjectBHSupport, ElementBHSupport, mts, rds, UniverseExtension, TechPackTPIde) = False Then
            Trace.WriteLine("Universe upgrade stopped while adding objects.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If

        If Universe_AddComputedObjects(Univ, ObjectBHSupport, ElementBHSupport, mts, rds, UniverseExtension, TechPackTPIde) = False Then
            Trace.WriteLine("Universe upgrade stopped while adding computed objects.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If

        System.Threading.Thread.Sleep(5000)

        If Universe_AddConditions(Univ, ObjectBHSupport, ElementBHSupport, mts, rds, UniverseExtension) = False Then
            Trace.WriteLine("Universe upgrade stopped while adding conditions.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If

        'obsolete check not done for this
        Universe_AddIncompatibleObjects(Univ, mts)

        'check obsolete content
        Dim Tbl As Designer.Table
        Dim Jn As Designer.Join
        Dim Cntxt As Designer.Context
        Dim Cls As Designer.Class
        Dim count As Integer
        Dim found As Boolean

        Trace.WriteLine(" ")
        Trace.WriteLine("Obsolete information: " & Univ.LongName & " (" & SaveFileName & ")")
        Trace.WriteLine("Obsolete universe tables:")
        For count = 1 To Univ.Tables.Count
            Tbl = Univ.Tables.Item(count)
            If InStrRev(updatedTables, Tbl.Name & ";") = 0 Then
                Trace.WriteLine(Tbl.Name)
            End If
        Next count

        Trace.WriteLine(" ")
        Trace.WriteLine("Obsolete universe joins:")
        For Each Jn In Univ.Joins
            If InStrRev(updatedJoins, Jn.Expression & ";") = 0 Then
                Trace.WriteLine(Jn.Expression)
            End If
        Next Jn

        Trace.WriteLine(" ")
        Trace.WriteLine("Obsolete universe contexts:")
        For Each Cntxt In Univ.Contexts
            If InStrRev(updatedContexts, Cntxt.Name & ";") = 0 Then
                Trace.WriteLine(Cntxt.Name)
            End If
        Next Cntxt

        Trace.WriteLine(" ")
        Trace.WriteLine("Obsolete universe classes:")
        For Each Cls In Univ.Classes
            checkClass(Cls, updatedClasses)
        Next Cls

        Trace.WriteLine(" ")
        Trace.WriteLine("Obsolete universe objects:")
        For Each Cls In Univ.Classes
            checkObject(Cls, updatedObjects)
        Next Cls

        Trace.WriteLine(" ")
        Trace.WriteLine("Obsolete universe conditions:")
        For Each Cls In Univ.Classes
            checkCondition(Cls, updatedConditions)
        Next Cls

        'remove obsolete objects under "Computed Counters"
        If (EBSTechPack = True) Then
            Trace.WriteLine(" ")
            Trace.WriteLine("Obsolete computed counters for EBS:")
            For Each Cls In Univ.Classes()
                If Cls.Name = "Computed Counters" Then
                    checkandRemoveObject(Cls, updatedObjects)
                End If
            Next Cls
        End If

        If SaveFileName <> Univ.Name Or BoVersion = "XI" Then
            retry = True
            While retry = True
                Try
                    retry = False
                    If OutputDir_Original <> "" Then
                        OutputDir = getUniverseSaveDirectory(OutputDir_Original)
                        Try
                            If Not System.IO.Directory.Exists(OutputDir) Then
                                System.IO.Directory.CreateDirectory(OutputDir)
                            End If
                        Catch ex As Exception
                            Console.WriteLine("Create Directory '" & OutputDir & "' failed: " & ex.ToString)
                        End Try
                        Univ.SaveAs(OutputDir & "\" & SaveFileName)

                        'Modified for HK80815 -Starts
                        Console.WriteLine("The Universe " & SaveFileName & "has been successfully updated and saved to the following path: " & OutputDir & "\" & ".")
                        'Modified for HK80815 -Ends
                        tpUtilities.displayMessageBox("The Universe " & SaveFileName & " has been successfully updated and saved to the following path: " _
                                      & OutputDir & "\" & ".", MsgBoxStyle.OkOnly, "Upgrade successful")
                    Else
                        Univ.SaveAs(SaveFileName)
                        'Modified for HK80815 -Starts
                        Console.WriteLine("The Universe " & SaveFileName & "has been successfully updated")
                        'Modified for HK80815 -Ends
                        tpUtilities.displayMessageBox("The Universe " & SaveFileName & " has been successfully updated", MsgBoxStyle.OkOnly, "Upgrade successful")
                    End If

                Catch ex As Exception
                    Trace.WriteLine("Error saving universe: " & ex.ToString())
                    tpUtilities.displayMessageBox("Error saving universe", MsgBoxStyle.Critical, "Error")
                    If OutputDir <> "" Then
                        Console.WriteLine("Saving failed to : " & OutputDir & "\" & SaveFileName & ".")
                    Else
                        Console.WriteLine("Saving failed to : " & SaveFileName & ".")
                    End If

                    System.Threading.Thread.Sleep(2000)
                    'retry = True
                End Try
            End While
        Else
            retry = True
            While retry = True
                Try
                    retry = False
                    Univ.Save()
                Catch ex As Exception
                    Console.WriteLine("Saving failed to : " & Univ.Name & ".")
                    System.Threading.Thread.Sleep(2000)
                    'retry = True
                End Try
            End While
        End If

        retry = True
        While retry = True
            Try
                retry = False
                Univ.Close()
            Catch ex As Exception
                Console.WriteLine("Closing failed of : " & Univ.Name & ".")
            End Try
        End While

        Return True

    End Function

    Protected Overridable Function getUniverseSaveDirectory(ByVal originalDirectory As String) As String
        Return originalDirectory & "\unv"
    End Function

    Private Function checkandRemoveObject(ByRef Cls As Designer.Class, ByRef checkString As String) As Boolean
        Dim SubCls As Designer.Class
        Dim Obj As Designer.Object
        For Each Obj In Cls.Objects
            If InStrRev(checkString, Cls.Name & "/" & Obj.Name & ";") = 0 Then
                Trace.WriteLine("Removing " & Cls.Name & "/" & Obj.Name)
                Obj.Delete()
            End If
        Next
        For Each SubCls In Cls.Classes
            checkandRemoveObject(SubCls, checkString)
        Next
    End Function


    Private Function checkClass(ByRef Cls As Designer.Class, ByRef checkString As String) As Boolean
        Dim SubCls As Designer.Class
        If InStrRev(checkString, Cls.Name & ";") = 0 Then
            Trace.WriteLine(Cls.Name)
        End If
        For Each SubCls In Cls.Classes
            checkClass(SubCls, checkString)
        Next
    End Function
    Private Function checkObject(ByRef Cls As Designer.Class, ByRef checkString As String) As Boolean
        Dim SubCls As Designer.Class
        Dim Obj As Designer.Object
        For Each Obj In Cls.Objects
            If InStrRev(checkString, Cls.Name & "/" & Obj.Name & ";") = 0 Then
                Trace.WriteLine(Cls.Name & "/" & Obj.Name)
            End If
        Next
        For Each SubCls In Cls.Classes
            checkObject(SubCls, checkString)
        Next
    End Function
    Private Function checkCondition(ByRef Cls As Designer.Class, ByRef checkString As String) As Boolean
        Dim SubCls As Designer.Class
        Dim Cond As Designer.PredefinedCondition
        For Each Cond In Cls.PredefinedConditions
            If InStrRev(checkString, Cls.Name & "/" & Cond.Name & ";") = 0 Then
                Trace.WriteLine(Cls.Name & "/" & Cond.Name)
            End If
        Next
        For Each SubCls In Cls.Classes
            checkCondition(SubCls, checkString)
        Next
    End Function

    '' 
    ' Get universe objects (defined in Universe, Objects tab in IDE)
    ' This allows us to check if an object is defined in the tech pack before we add it to 
    ' a verification report.
    Private Sub getObjectsForVerificationReports()

        Dim bo_objects As BOObjectsTPIde = New BOObjectsTPIde(New UniverseProxy())

        For count As Integer = 1 To UnvMts.Count
            Dim universeMT As UniverseMeasurementsTPIde.UniverseMeasurement = UnvMts.Item(count)

            ObjectBHSupport = getObjectBHSupport(universeMT.MeasurementTypes)
            ElementBHSupport = getElementBHSupport(universeMT.MeasurementTypes)

            Dim result As Boolean
            result = bo_objects.getObjectsFromDatabase(TechPackName, TPVersion, tpConn, dbCommand, dbReader, UnvMt.MeasurementTypes, _
                                  ObjectBHSupport, ElementBHSupport, UnvMt.UniverseNameExtension, TechPackTPIde, False)
            result = bo_objects.getObjectsFromDatabase(TechPackName, TPVersion, tpConn, dbCommand, dbReader, UnvMt.MeasurementTypes, _
                                  ObjectBHSupport, ElementBHSupport, UnvMt.UniverseNameExtension, BaseTechPackTPIde, False)

            ' Check the result:
            If (result = False) Then
                Trace.WriteLine("Error reading objects from dwhrep database for verification reports.")
            End If
        Next
    End Sub

    'Function MakeVerificationReports(ByRef boUser As String, ByRef boPass As String, ByRef boRep As String, ByRef BoApp As busobj.Application, _
    '                                 ByRef Filename As String, ByRef BaseFilename As String, ByRef CMTechPack As Boolean, ByRef OutputDir_Original As String, _
    '                                 ByRef EniqEnvironment As String, ByRef BoVersion As String, ByRef BoAut As String) As Boolean
    '    Dim classNameAndFunction = className & "," & GetCurrentMethod.Name & ": "
    '    Console.WriteLine("Creating verification reports. Please ensure that the universe has been updated before running this command")

    '    Dim Found As Boolean
    '    Dim ReportList As String
    '    Dim ResultCount As MsgBoxResult
    '    Dim ResultDay As MsgBoxResult
    '    Dim ResultDayBH As MsgBoxResult
    '    Dim ResultElemBH As MsgBoxResult
    '    Dim ClsInit As Integer
    '    Dim OutputDir As String
    '    Dim count As Integer
    '    totalReportsCreated = 0
    '    Me.BoApp = BoApp

    '    ' Initialise excluded key array:
    '    excludedKeyObjects = New String() {"hours from now", "datetime (raw)", "Datetime (UTC)", "Busy Hour Type", "Min"}
    '    excludedKeyObjectsForRankBH = New String() {"hours from now", "datetime (raw)", "Datetime (UTC)", "Object Type"}

    '    FullAware = True

    '    TechPackTPIde = Filename
    '    BaseTechPackTPIde = BaseFilename

    '    Try
    '        tpAdoConn = "DSN=" & EniqEnvironment & ";"
    '        baseAdoConn = "DSN=" & EniqEnvironment & ";"

    '        tpConn = New System.Data.Odbc.OdbcConnection()
    '        tpConn.ConnectionString = tpAdoConn
    '        baseConn = New System.Data.Odbc.OdbcConnection()
    '        baseConn.ConnectionString = baseAdoConn
    '    Catch ex As Exception
    '        Trace.WriteLine("ODBC Exception: " + ex.ToString)
    '        Return False
    '    End Try

    '    Try
    '        tpConn.Open()
    '        baseConn.Open()
    '    Catch ex As Exception
    '        Trace.WriteLine("Failed to open dwhrep connection for tech pack data. ODBC Exception: " + ex.ToString)
    '        Return False
    '    End Try

    '    Try
    '        BoApp.Visible = False
    '        BoApp.Interactive = False
    '        If BoVersion = "6.5" Then
    '            BoApp.LoginAs(boUser, boPass, False, boRep)
    '        ElseIf BoVersion = "XI" Then
    '            'BoApp.LogonDialog()
    '            'BoApp().Logon("Administrator", "", "dcweb4-a:6400", "Enterprise")
    '            BoApp.Logon(boUser, boPass, boRep, BoAut, False, False)
    '        Else
    '            Trace.WriteLine("Invalid BO Version. Contact ENIQ support.")
    '            tpConn.Close()
    '            baseConn.Close()
    '            Try
    '                BoApp.Quit()
    '            Catch ee As Exception
    '                Trace.WriteLine("Error closing BusinessIntelligence application.")
    '            End Try
    '            Return False
    '        End If

    '    Catch ex As Exception
    '        Try
    '            Trace.WriteLine("BO Exception: " + ex.ToString)
    '            Trace.WriteLine("Using manual logon.")
    '            BoApp.LogonDialog()
    '        Catch e As Exception
    '            tpConn.Close()
    '            baseConn.Close()
    '            Try
    '                BoApp.Quit()
    '            Catch ee As Exception
    '                Trace.WriteLine("Error closing BusinessIntelligence application.")
    '            End Try
    '            Return False
    '        End Try
    '    End Try

    '    ' Fix for HK42800:
    '    ' Log on to design app:
    '    Try
    '        DesignerApp = tpUtilities.setupDesignerApp(BoVersion, boUser, boPass, boRep, BoAut)
    '    Catch ex As Exception
    '        Trace.WriteLine(classNameAndFunction & "Error setting up designer application, exiting.")
    '        Return False
    '    End Try

    '    ClsInit = 0

    '    ClsInit = Initialize_Classes(OutputDir_Original)
    '    If ClsInit = 1 Then
    '        tpConn.Close()
    '        baseConn.Close()
    '        BoApp.Quit()
    '    Else
    '        ' Go through all of the universes:
    '        For count = 1 To UnvMts.Count
    '            UnvMt = UnvMts.Item(count)
    '            Try
    '                ' Open universe:
    '                universeForReports = tpUtilities.promptToOpenUniverse(UnvMt.UniverseExtension, UnvMt.UniverseNameExtension, BoVersion, _
    '                                                               DesignerApp, UniverseName, UniverseFileName, OutputDir_Original)
    '            Catch ex As Exception
    '                Trace.WriteLine(classNameAndFunction & "Error opening universe while generating reports, exiting: " & ex.ToString())
    '                tpUtilities.displayMessageBox("Error opening universe while generating reports", MsgBoxStyle.Exclamation, "Error opening universe")
    '                Exit For
    '            End Try

    '            ' Get universe objects (defined in Universe, Objects tab in IDE):
    '            getObjectsForVerificationReports()

    '            ' Find the hidden objects in the universe (clear list for previous universe):
    '            hiddenObjects.Clear()
    '            hiddenObjects = getHiddenObjects(universeForReports.Classes, hiddenObjects)
    '            Try
    '                CreateVerificationReports(BoApp, CMTechPack, OutputDir_Original, BoVersion, UnvMt.MeasurementTypes, _
    '                                                         UnvMt.UniverseExtension)
    '            Catch reportException As Exception
    '                Trace.WriteLine(classNameAndFunction & "Error generating reports, exiting: " & reportException.ToString())
    '                tpUtilities.displayMessageBox("There was an error creating the reports. " & totalReportsCreated & " report(s) written to directory " & _
    '                                  OutputDir_Original, MsgBoxStyle.Critical, "Error creating verification reports")
    '            Catch nullException As System.NullReferenceException
    '                Trace.WriteLine(classNameAndFunction & "Error generating reports, exiting: " & nullException.ToString())
    '                tpUtilities.displayMessageBox("There was an error creating the reports. " & totalReportsCreated & " report(s) written to directory " & _
    '                                  OutputDir_Original, MsgBoxStyle.Critical, "Error creating verification reports")
    '            End Try
    '        Next count
    '        tpUtilities.displayMessageBox("Finished generating verification reports. " & totalReportsCreated & " report(s) written to directory " & _
    '                                  OutputDir_Original, MsgBoxStyle.OkOnly, "Finished creating verification reports")
    '    End If

    '    tpConn.Close()
    '    baseConn.Close()
    '    BoApp.Interactive = True
    '    DesignerApp.Quit()
    '    Return True

    'End Function

    ''
    'Gets the save file name for the universe.
    '@param dceName         The short name (e.g. DCE1)
    '@param dceExtension    The short extension (e.g. a)
    '@param fullName        The universe's full name.
    '@param fullExtension   The full extension
    '@param BoVersion The BO version.
    '@returns               The universe file name.
    Public Function getUniverseFilename(ByVal dceName As String, ByVal dceExtension As String, _
                                        ByVal fullName As String, ByVal fullExtension As String, _
                                        ByVal BoVersion As String) As String
        Trace.WriteLine("UniverseFunctionsTPIde,  getUniverseFilename(): Entering")
        Dim SaveFileName As String = ""
        Dim defaultName As String = "Universe"

        Try
            If BoVersion = "6.5" Then
                If dceExtension <> "" Then
                    SaveFileName = dceName & dceExtension
                Else
                    SaveFileName = dceName
                End If
            ElseIf BoVersion = "XI" Then
                If fullExtension <> "" Then
                    SaveFileName = fullName & " " & fullExtension
                Else
                    SaveFileName = fullName
                End If
            Else
                Trace.WriteLine("UniverseFunctionsTPIde,  getUniverseFilename(): Invalid BO Version. Contact support.")
            End If

            ' Check if we couldn't get the saveFileName:
            If (SaveFileName Is Nothing) OrElse (SaveFileName = "") Then
                Trace.WriteLine("UniverseFunctionsTPIde,  getUniverseFilename(): " _
                                & "File name for universe is not defined, using default name for the universe file.")
                ' Use default name for universe
                SaveFileName = defaultName
            End If
        Catch ex As Exception
            Trace.WriteLine("UniverseFunctionsTPIde,  getUniverseFilename(): Error getting save file name for the universe: " & ex.ToString())
            SaveFileName = defaultName
        End Try
        Console.WriteLine("Got file name universe will be saved to: " & SaveFileName)
        Trace.WriteLine("UniverseFunctionsTPIde,  getUniverseFilename(): Got file name universe will be saved to: " & SaveFileName)
        Trace.WriteLine("UniverseFunctionsTPIde,  getUniverseFilename(): Exiting")
        Return SaveFileName
    End Function

    'Function CreateVerificationReports(ByRef BoApp As busobj.Application, ByVal CMData As Boolean, ByRef OutputDir_Original As String, ByRef BOVersion As String, _
    '                                   ByRef mts As MeasurementTypesTPIde, ByRef UniverseExtension As String) As Boolean

    '    Dim Found As Boolean
    '    Dim ReportList As String
    '    Dim ResultCount As MsgBoxResult
    '    Dim ResultDay As MsgBoxResult
    '    Dim ResultDayBH As MsgBoxResult
    '    Dim ResultElemBH As MsgBoxResult
    '    Dim ResultRankBH As MsgBoxResult
    '    Dim OutputDir As String

    '    FullAware = True

    '    OutputDir = OutputDir_Original & "\rep"
    '    Try
    '        If Not System.IO.Directory.Exists(OutputDir) Then
    '            System.IO.Directory.CreateDirectory(OutputDir)
    '        End If
    '    Catch ex As Exception
    '        Console.WriteLine("Create Directory '" & OutputDir & "' failed: " & ex.ToString)
    '        Return False
    '    End Try

    '    Found = False
    '    ReportList = ""
    '    Found = updateReportList("COUNT", ReportList, mts)
    '    If Found = True Then
    '        ResultCount = askWhichReportsToCreate("COUNT Total", ReportList)
    '    End If

    '    Found = False
    '    ReportList = ""
    '    Found = updateReportList("DAY", ReportList, mts)
    '    If Found = True Then
    '        ResultDay = askWhichReportsToCreate("DAY Total", ReportList)
    '    End If

    '    Found = False
    '    ReportList = ""
    '    Found = updateReportList("DAYBH", ReportList, mts)
    '    If Found = True Then
    '        ResultDayBH = askWhichReportsToCreate("DAYBH Busy Hour", ReportList)
    '    End If

    '    Found = False
    '    ReportList = ""
    '    Found = updateReportList("ELEMBH", ReportList, mts)
    '    If Found = True Then
    '        ResultElemBH = askWhichReportsToCreate("ELEMBH Busy Hour", ReportList)
    '    End If

    '    Found = False
    '    ReportList = ""
    '    Found = updateReportList("RANKBH", ReportList, mts)
    '    If Found = True Then
    '        ResultRankBH = askWhichReportsToCreate("Rank Busy Hour", ReportList)
    '        End If

    '        'Create Count verification reports
    '        If ResultCount = MsgBoxResult.Yes Then
    '        Console.WriteLine("Creating Count verification reports")
    '        Console.WriteLine("UniverseExtension = " & UniverseExtension)
    '        VerifReports_makeVerificationReport_Count(BoApp, OutputDir, False, mts, UniverseExtension, BOVersion)
    '        End If
    '        If ResultCount = MsgBoxResult.No Then
    '        Console.WriteLine("Creating Count verification reports")
    '        VerifReports_makeVerificationReport_Count(BoApp, OutputDir, True, mts, UniverseExtension, BOVersion)
    '        End If
    '        'Create Day verification reports
    '        If ResultDay = MsgBoxResult.Yes Then
    '            Console.WriteLine("Creates Day/Total verification reports")
    '        VerifReports_makeVerificationReport_Day(BoApp, OutputDir, False, mts, UniverseExtension, BOVersion)
    '        End If
    '        If ResultDay = MsgBoxResult.No Then
    '            Console.WriteLine("Creates Day/Total verification reports")
    '        VerifReports_makeVerificationReport_Day(BoApp, OutputDir, True, mts, UniverseExtension, BOVersion)
    '        End If
    '        'Create DAYBH Busy Hour verification reports
    '        If ResultDayBH = MsgBoxResult.Yes Then
    '        Console.WriteLine("Creating DAYBH Busy Hour verification reports")
    '        VerifReports_makeVerificationReport_DayBH(BoApp, OutputDir, False, mts, UniverseExtension, BOVersion)
    '        End If
    '        If ResultDayBH = MsgBoxResult.No Then
    '        Console.WriteLine("Creating DAYBH Busy Hour verification reports")
    '        VerifReports_makeVerificationReport_DayBH(BoApp, OutputDir, True, mts, UniverseExtension, BOVersion)
    '        End If

    '        'Create ELEMBH Busy Hour verification reports
    '        If ResultElemBH = MsgBoxResult.Yes Then
    '        Console.WriteLine("Creating ELEMBH Busy Hour verification reports")
    '        VerifReports_makeVerificationReport_ElemBH(BoApp, OutputDir, False, mts, UniverseExtension, BOVersion)
    '        End If
    '        If ResultElemBH = MsgBoxResult.No Then
    '        Console.WriteLine("Creating ELEMBH Busy Hour verification reports")
    '        VerifReports_makeVerificationReport_ElemBH(BoApp, OutputDir, True, mts, UniverseExtension, BOVersion)
    '        End If

    '    'Create RANKBH Busy Hour verification reports
    '    If ResultRankBH = MsgBoxResult.Yes Then
    '        Console.WriteLine("Creating RANKBH Busy Hour verification reports")
    '        VerifReports_makeVerificationReport_RankBH(BoApp, OutputDir, False, mts, UniverseExtension, BOVersion)
    '    End If
    '    If ResultRankBH = MsgBoxResult.No Then
    '        Console.WriteLine("Creating RANKBH Busy Hour verification reports")
    '        VerifReports_makeVerificationReport_RankBH(BoApp, OutputDir, True, mts, UniverseExtension, BOVersion)
    '    End If

    '    Return True
    'End Function

    ''
    '@returns
    '@remarks
    Public Function updateReportList(ByVal reportType As String, ByRef ReportList As String, _
                                      ByRef mts As MeasurementTypesTPIde) As Boolean
        Dim found As Boolean
        found = False

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.MeasurementTypeID <> "" Then
                If checkMeasurementType(reportType, mt) Then
                    If (ReportList.Contains(mt.TypeName) = False) Then
                        found = True
                        ReportList &= mt.TypeName & ", "
                    End If
                End If
            End If
        Next mt_count
        Return found
    End Function

    ''
    ' This function takes measurement type and checks if the specified report type can be created for it.
    ' e.g. check if a Count or Day report can be created for a given measurement type.
    ' 
    '@param reportType The type of report: Count, Day/Total, Day BH, Element BH, Rank BH.
    '@param mt The measurement type object.
    '@returns A boolean value. True if a report can be created for this measurement type.
    Public Function checkMeasurementType(ByVal reportType As String, ByRef mt As MeasurementTypesTPIde.MeasurementType) As Boolean
        Dim found As Boolean
        found = False

        If (mt Is Nothing) OrElse (reportType Is Nothing) Then
            ' Return false if the measurement type is null:
            Return found
        End If

        If (reportType.ToUpper = "COUNT") Then
            found = mt.CreateCountTable
        ElseIf (reportType.ToUpper = "DAY") Then
            If (mt.MeasurementTypeID <> "") Then ' Create Day reports for all measurement types
                found = True
            Else
                found = False
            End If
        ElseIf (reportType.ToUpper = "DAYBH") Then
            If (mt.RankTable = False And mt.ObjectBusyHours <> "") Then
                found = True
            Else
                found = False
            End If
        ElseIf (reportType.ToUpper = "ELEMBH") Then
            If (mt.RankTable = False And mt.ElementBusyHours = True) Then
                found = True
            Else
                found = False
            End If
        ElseIf (reportType.ToUpper = "RANKBH") Then
            If (mt.RankTable = True) Then ' RankTable is a boolean
                found = True
            Else
                found = False
            End If
        End If
        Return found
    End Function

    ''
    ' Shows a message box to ask which reports will be created.
    ' 
    '@param reportType The type of the report (Count, ELEMBH, RankBH etc)
    '@param reportList The complete list of reports to be created.
    '@returns A MsgBoxResult value depending on what the users selects.
    '@remarks WI 2.11
    Private Function askWhichReportsToCreate(ByVal reportType As String, ByVal reportList As String) As MsgBoxResult
        Dim message As String
        message = "Following " & reportType & " Verification Reports should be created." & Chr(10) & reportList & Chr(10) & _
            "Do you want to create them all? " & Chr(10) & _
            "Select Yes to create all." & Chr(10) & _
            "Select No to verify creation per measurement." & Chr(10) & _
            "Select Cancel to skip report creation."

        Dim result As MsgBoxResult
        result = tpUtilities.displayMessageBox(message, MsgBoxStyle.YesNoCancel, reportType & " reports")
        Return result
    End Function

    ''
    'Updates the version properties.
    '@param     OutputDir_Original  Output directory.
    Public Function UpdateVersionProperties(ByRef OutputDir_Original As String) As Boolean
        Dim metafile As String
        Dim iFileNum As Short
        Dim count As Integer
        Dim OutputDir As String
        Dim readString As String

        OutputDir = OutputDir_Original & "\install"
        Try
            If Not System.IO.Directory.Exists(OutputDir) Then
                System.IO.Directory.CreateDirectory(OutputDir)
            End If
        Catch ex As Exception
            Console.WriteLine("Create Directory '" & OutputDir & "' failed: " & ex.ToString)
        End Try

        metafile = OutputDir & "\version.properties"

        'read file information
        Dim buildTag As String
        Dim buildNumber As String
        Dim requiredTechPack As String
        Dim requiredList() As String
        Try
            iFileNum = FreeFile()
            FileOpen(iFileNum, metafile, OpenMode.Input)
            Do Until EOF(iFileNum)
                readString = LineInput(iFileNum)
                If InStrRev(readString, "build.tag=") > 0 Then
                    buildTag = readString
                End If
                If InStrRev(readString, "build.buildnumber=") > 0 Then
                    buildNumber = readString
                End If
                If InStrRev(readString, "required_tech_packs.") > 0 Then
                    requiredTechPack &= Replace(readString, "required_tech_packs.", "") & ","
                End If
            Loop
            FileClose(iFileNum)
        Catch ex As Exception
            'do nothing
        End Try

        Try
            If System.IO.File.Exists(metafile) Then
                Kill(metafile)
            End If
        Catch ex As Exception
            'do nothing
        End Try


        iFileNum = FreeFile()
        FileOpen(iFileNum, metafile, OpenMode.Output)
        PrintLine(iFileNum, "#", Now())
        PrintLine(iFileNum, "tech_pack.name=" & TechPackName)
        PrintLine(iFileNum, "tech_pack.version=" & TPReleaseVersion)
        If buildTag Is Nothing Then
            PrintLine(iFileNum, "build.tag=")
        Else
            PrintLine(iFileNum, buildTag)
        End If
        If buildNumber Is Nothing Then
            PrintLine(iFileNum, "build.buildnumber=")
        Else
            PrintLine(iFileNum, buildNumber)
        End If
        PrintLine(iFileNum, "universe_build.buildnumber=" & TPVersion)
        If Not requiredTechPack Is Nothing Then
            requiredList = Split(requiredTechPack)
            For count = 0 To UBound(requiredList) - 1
                PrintLine(iFileNum, "required_tech_packs." & requiredList(count))
            Next count
        End If

        FileClose(iFileNum)

        Return True
    End Function

    ''
    ' Makes universe.
    ' @param Filename Specifies TP definition's filename.
    ' @param CMTechPack Specifies tech pack type. Value is True if tech tech pack is CM. Value is False if tech tech pack is PM.
    ' @param BaseFilename Specifies base definition's filename.
    Function MakeUniverse(ByRef boUser As String, ByRef boPass As String, ByRef boRep As String, ByRef Filename As String, ByRef CMTechPack As Boolean, _
                          ByRef BaseFilename As String, ByRef OutputDir_Original As String, ByRef EniqEnvironment As String, ByRef BoVersion As String, _
                          ByRef BoAut As String) As Boolean
        Dim classNameAndFunction = className & "," & GetCurrentMethod.Name & ": "
        'Dim DesignerApp As Designer.Application

        Dim retry As Boolean
        Dim ClsInit As Integer
        Dim Univ As Designer.Universe
        Dim Result As MsgBoxResult
        Dim checkItems As Designer.CheckedItems
        Dim checkItem As Designer.CheckedItem
        Dim OutputDir As String
        Dim count As Integer

        extra_joins = New UnivJoinsTPIde

        FullAware = True

        'update build number
        'Dim tp_excel = New TPExcelWriter
        'Dim updateBuild = tp_excel.updateBuildNumber(Filename, "universe", OutputDir_Original)
        'tp_excel = Nothing

        TechPackTPIde = Filename
        BaseTechPackTPIde = BaseFilename


        Try
            tpAdoConn = "DSN=" & EniqEnvironment & ";"
            baseAdoConn = "DSN=" & EniqEnvironment & ";"
            tpConn = New System.Data.Odbc.OdbcConnection()
            tpConn.ConnectionString = tpAdoConn
            baseConn = New System.Data.Odbc.OdbcConnection()
            baseConn.ConnectionString = baseAdoConn
        Catch ex As Exception
            Trace.WriteLine("ODBC Exception: " + ex.ToString)
            Return False
        End Try

        Try
            tpConn.Open()
            baseConn.Open()
        Catch ex As Exception
            Trace.WriteLine("ODBC Exception: " + ex.ToString)
            Return False
        End Try

        Try
            DesignerApp = tpUtilities.setupDesignerApp(BoVersion, boUser, boPass, boRep, BoAut)
        Catch ex As Exception
            Trace.WriteLine(classNameAndFunction & "Error setting up designer application, exiting.")
            Return False
        End Try

        ClsInit = 0

        ClsInit = Initialize_Classes(OutputDir_Original)
        UpdateVersionProperties(OutputDir_Original)
        Try
        If ClsInit = 1 Then
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
        Else
            For count = 1 To UnvMts.Count
                UnvMt = UnvMts.Item(count)
                    Dim createSuccessful As Boolean = createUniverse(DesignerApp, UnvMt.MeasurementTypes, _
                UnvMt.ReferenceTypes, UnvMt.VectorReferenceTypes, _
                UnvMt.UnivJoins, UnvMt.ReferenceDatas, UnvMt.VectorReferenceDatas, _
                    OutputDir_Original, BoVersion, UnvMt.UniverseNameExtension, UnvMt.UniverseExtension, CMTechPack, count, TechPackTPIde)

                    If (createSuccessful = False) Then
                        Throw New Exception("Error creating universe: " & TechPackTPIde & " " & UnvMt.UniverseExtension)
                    End If
                Next count
            End If
        Catch ex As Exception
            Trace.WriteLine("Error creating universe: " & ex.ToString())
            Console.WriteLine("Error creating universe: " & ex.ToString())
            tpUtilities.displayMessageBox("Create universe failed", MsgBoxStyle.Critical, "Create universe failed")
        Catch nullException As System.NullReferenceException
            Trace.WriteLine("Error creating universe: " & nullException.ToString())
            Console.WriteLine("Null exception while upgrading universe: " & nullException.ToString())
            tpUtilities.displayMessageBox("Create universe failed", MsgBoxStyle.Critical, "Create universe failed")
        End Try

        tpConn.Close()
        baseConn.Close()
        DesignerApp.Visible = True
        DesignerApp.Interactive = True
        DesignerApp.Quit()

        Return True
    End Function

    Function createUniverse(ByVal DesignerApp As Designer.Application, ByRef mts As MeasurementTypesTPIde, ByRef rts As ReferenceTypesTPIde, _
    ByRef vector_rts As ReferenceTypesTPIde, ByRef univ_joins As UnivJoinsTPIde, ByRef rds As ReferenceDatasTPIde, ByRef vector_rds As ReferenceDatasTPIde, _
    ByRef OutputDir_Original As String, ByRef BOVersion As String, ByRef UniverseExtension As String, ByRef UniverseNameExtension As String, _
    ByRef CMTechPack As Boolean, ByRef UniverseCount As Integer, ByVal TechPackTPIde As String) As Boolean

        Dim retry As Boolean
        Dim ClsInit As Integer
        Dim Univ As Designer.Universe
        Dim OutputDir As String

        extra_joins = New UnivJoinsTPIde

        FullAware = True

        retry = True
        While retry = True
            Try
                retry = False
                Univ = DesignerApp.Universes.Add
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                DesignerApp.Visible = False
                DesignerApp.Interactive = False
            Catch ex As Exception
                System.Threading.Thread.Sleep(2000)
                retry = True
            End Try
        End While

        System.Threading.Thread.Sleep(2000) ' Sleep for 2 seconds

        ObjectBHSupport = getObjectBHSupport(mts)
        ElementBHSupport = getElementBHSupport(mts)

        If UniverseNameExtension <> "" Then
            SetParameter(Univ, "Name", UniverseName & " " & UniverseNameExtension)
            Trace.WriteLine("Log on Create Universe: " & UniverseName & " " & UniverseNameExtension)
        Else
            SetParameter(Univ, "Name", UniverseName)
            Trace.WriteLine("Log on Create Universe: " & UniverseName)
        End If

        SetParameter(Univ, "Description", UniverseDescription)
        Try
            SetParameter(Univ, "Connection", DesignerApp.Connections(1).Name)
        Catch ex As Exception
            Trace.WriteLine("Universe Connection Error: " & ex.Message & ".")
        End Try


        If Universe_AddTables(Univ, ObjectBHSupport, ElementBHSupport, True, mts, rts, vector_rts, UniverseExtension) = False Then
            Trace.WriteLine("Universe creation stopped while adding tables.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If

        If Universe_AddJoins(Univ, mts, univ_joins, extra_joins) = False Then
            Trace.WriteLine("Universe creation stopped while adding joins.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If

        'Univ.RefreshStructure()
        'Univ.ArrangeTables()

        Try
            Univ.ControlOption.LimitSizeofResultSet = True
            Univ.ControlOption.LimitExecutionTime = True
            Univ.ControlOption.LimitSizeOfLongTextObject = False
            Univ.ControlOption.WarnIfCostEstimateExceeded = False

            Univ.ControlOption.LimitSizeofResultSetValue = 250000
            Univ.ControlOption.LimitExecutionTimeValue = 120
        Catch ex As Exception
            Trace.WriteLine("Universe Control Option Exception: " & ex.ToString)
        End Try

        If Universe_AddClasses(Univ, ObjectBHSupport, ElementBHSupport, True, UniverseExtension) = False Then
            Trace.WriteLine("Universe creation stopped while adding classes.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If

        If Universe_AddCounters(Univ, CMTechPack, False, mts, vector_rds) = False Then
            Trace.WriteLine("Universe creation stopped while adding counters.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If


        'If Universe_AddComputedCounters(Univ, CMTechPack, False, mts, vector_rds) = False Then
        'Trace.WriteLine("Universe creation stopped while adding computed counters.")
        'tpConn.Close()
        'baseConn.Close()
        'DesignerApp.Quit()
        'Return logs
        'End If

        If Universe_AddCounterKeys(Univ, mts) = False Then
            Trace.WriteLine("Universe creation stopped while adding counter keys.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If

        'If Universe_AddComputedCounterKeys(Univ, mts) = False Then
        'Trace.WriteLine("Universe creation stopped while adding counter keys.")
        'tpConn.Close()
        'baseConn.Close()
        'DesignerApp.Quit()
        'Return logs
        'End If

        If Universe_AddAdditionalObjectsAndConditions(Univ, mts) = False Then
            Trace.WriteLine("Universe creation stopped while adding additional objects and conditions. Please, Contact Support.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If

        'If Universe_AddAdditionalComputedObjectsAndConditions(Univ, mts) = False Then
        'Trace.WriteLine("Universe creation stopped while adding additional computed objects and conditions. Please, Contact Support.")
        'tpConn.Close()
        'baseConn.Close()
        'DesignerApp.Quit()
        'Return logs
        'End If

        If Universe_AddObjects(Univ, ObjectBHSupport, ElementBHSupport, mts, rds, UniverseExtension, TechPackTPIde) = False Then
            Trace.WriteLine("Universe creation stopped while adding objects.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If


        If Universe_AddComputedObjects(Univ, ObjectBHSupport, ElementBHSupport, mts, rds, UniverseExtension, TechPackTPIde) = False Then
            Trace.WriteLine("Universe creation stopped while adding objects.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If

        System.Threading.Thread.Sleep(5000)

        If Universe_AddConditions(Univ, ObjectBHSupport, ElementBHSupport, mts, rds, UniverseExtension) = False Then
            Trace.WriteLine("Universe creation stopped while adding conditions.")
            tpConn.Close()
            baseConn.Close()
            DesignerApp.Quit()
            Return False
        End If

        Universe_AddIncompatibleObjects(Univ, mts)

        ' Get the universe file name:
        Dim SaveFileName = getUniverseFilename(UniverseFileName, UniverseExtension, UniverseName, UniverseNameExtension, BOVersion)

        retry = True
        While retry = True
            Try
                retry = False
                If OutputDir_Original <> "" Then
                    OutputDir = OutputDir_Original & "\unv"
                    Try
                        If Not System.IO.Directory.Exists(OutputDir) Then
                            System.IO.Directory.CreateDirectory(OutputDir)
                        End If
                    Catch ex As Exception
                        Console.WriteLine("Create Directory '" & OutputDir & "' failed: " & ex.ToString)
                    End Try
                    Univ.SaveAs(OutputDir & "\" & SaveFileName)
                    Console.WriteLine("Finished creating the universe to : " & OutputDir & "\" & SaveFileName & ".")
                    tpUtilities.displayMessageBox("Finished creating the universe: " & OutputDir & "\" & SaveFileName, MsgBoxStyle.OkOnly, "Finished creating universe")
                Else
                    Univ.SaveAs(SaveFileName)
                    Console.WriteLine("Finished creating the universe to : " & SaveFileName & ".")
                    tpUtilities.displayMessageBox("Finished creating the universe: " & SaveFileName, MsgBoxStyle.OkOnly, "Finished creating universe")
                End If
            Catch ex As Exception
                If OutputDir <> "" Then
                    Console.WriteLine("Saving failed to : " & OutputDir & "\" & SaveFileName & ".")
                    tpUtilities.displayMessageBox("Failed to save universe to " & OutputDir & "\" & SaveFileName & ".", MsgBoxStyle.OkOnly, "Failed to create universe")
                Else
                    Console.WriteLine("Saving failed to : " & SaveFileName & ".")
                    tpUtilities.displayMessageBox("Failed to save universe to " & SaveFileName & ".", MsgBoxStyle.OkOnly, "Failed to create universe")
                End If

                System.Threading.Thread.Sleep(2000)
                'retry = True
            End Try
        End While

        retry = True
        While retry = True
            Try
                retry = False
                Univ.Close()
            Catch ex As Exception
                Console.WriteLine("Closing failed of : " & SaveFileName & ".")
            End Try
        End While

        Return True

    End Function
    Sub SetParameter(ByRef Univ As Object, ByRef Parameter As String, ByRef Value As String)
        Dim retry As Boolean
        Dim try_count As Integer
        retry = True
        try_count = 1
        While retry = True
            Try
                retry = False
                If Parameter = "Name" Then
                    Univ.LongName = Value
                    Trace.WriteLine("UniverseFunctionsTPIde, SetParameter(): set parameter 'LongName' to value: " & Value)
                End If
                If Parameter = "Description" Then
                    Univ.Description = Value
                    Trace.WriteLine("UniverseFunctionsTPIde, SetParameter(): set parameter 'Description' to value: " & Value)
                End If
                If Parameter = "Connection" Then
                    Univ.Connection = Value
                    Trace.WriteLine("UniverseFunctionsTPIde, SetParameter(): set parameter 'Connection' to value: " & Value)
                End If
            Catch ex As Exception
                System.Threading.Thread.Sleep(5000)
                retry = True
                try_count += 1
                If try_count = 2 Then
                    Console.WriteLine("Set universe parameter '" + Parameter + "' to value '" + Value + "' manually.")
                    retry = False
                End If
            End Try
        End While
    End Sub

    '' 
    ' Checks if a tech pack exists in dwhrep database. Can be used to check for both main tech pack and base tech pack.
    '@param tpConn          The database connection object.
    '@param dbCommand       The ODBC command object.
    '@param dbReader        The ODBC reader object.
    '@param techPackName    The name of the tech pack.
    '@param tp_utils        TPUtilitiesTPIde used to read from the database.
    '@returns               The tech pack name (if the tech pack was found in the Versioning table in DWHREP), otherwise Nothing.
    Private Function getTPNameFromDB(ByRef tpConn As System.Data.Odbc.OdbcConnection, ByRef dbCommand As System.Data.Odbc.OdbcCommand, _
                                             ByRef dbReader As System.Data.Odbc.OdbcDataReader, ByRef techPackName As String, _
                                             ByRef tp_utils As TPUtilitiesTPIde) As String
        Dim techPackNameFromDB As String
        techPackNameFromDB = tp_utils.readSingleValue("SELECT TECHPACK_NAME FROM VERSIONING WHERE VERSIONID = ", tpConn, dbCommand, dbReader, techPackName)
        If techPackNameFromDB Is Nothing Then
            Console.WriteLine("Could not find data for " & techPackName & " using data source " & tpConn.DataSource)
            tpUtilities.displayMessageBox("Could not find data for " & techPackName & " using data source " & tpConn.DataSource, MsgBoxStyle.Critical, _
                   "Reading from 'dwhrep' database failed.")
            Trace.WriteLine("checkTechPackExistsInDB(): Reading from database failed. Exiting.")
        Else
            Trace.WriteLine("Found data for " & techPackName & " using data source " & tpConn.DataSource)
        End If
        Return techPackNameFromDB
    End Function
    ''
    ' Initializes classes. Reads TP definitions. 
    '
    ' @return 0, if initialization is successful
    Public Function Initialize_Classes(ByVal OutputDir_Original As String) As Integer

        Dim Description As String
        Dim tpUtilities = New TPUtilitiesTPIde
        Dim extendedUnv As String

        Try
            TechPackName = getTPNameFromDB(tpConn, dbCommand, dbReader, TechPackTPIde, tpUtilities)
            Dim baseTechPackName = getTPNameFromDB(tpConn, dbCommand, dbReader, BaseTechPackTPIde, tpUtilities)
        If (TechPackName = Nothing Or baseTechPackName = Nothing) Then
            Return 1
        End If

            TechPackType = tpUtilities.readSingleValue("SELECT TECHPACK_TYPE FROM VERSIONING WHERE  VERSIONID = ", tpConn, dbCommand, dbReader, TechPackTPIde)
            UniverseFileName = tpUtilities.readSingleValue("SELECT UNIVERSENAME FROM UNIVERSENAME WHERE  VERSIONID = ", tpConn, dbCommand, dbReader, TechPackTPIde)
            Description = tpUtilities.readSingleValue("SELECT DESCRIPTION FROM VERSIONING WHERE  VERSIONID = ", tpConn, dbCommand, dbReader, TechPackTPIde)
            TPReleaseVersion = tpUtilities.readSingleValue("SELECT TECHPACK_VERSION FROM VERSIONING WHERE VERSIONID = ", tpConn, dbCommand, dbReader, TechPackTPIde)
            TPVersion = tpUtilities.readSingleValue("SELECT VERSIONID FROM VERSIONING WHERE VERSIONID = ", tpConn, dbCommand, dbReader, TechPackTPIde)
            VendorRelease = tpUtilities.readSingleValue("SELECT VENDORRELEASE FROM SUPPORTEDVENDORRELEASE WHERE VERSIONID = ", tpConn, dbCommand, dbReader, TechPackTPIde)
            ProductNumber = tpUtilities.readSingleValue("SELECT PRODUCT_NUMBER FROM VERSIONING WHERE VERSIONID = ", tpConn, dbCommand, dbReader, TechPackTPIde)

        DefaultKeyMaxAmount = 255 'tp_utils.readSingleValue("Coversheet$B7:B8", baseConn, dbCommand, dbReader)
        DefaultCounterMaxAmount = 255 ' tp_utils.readSingleValue("Coversheet$B8:B9", baseConn, dbCommand, dbReader)
        CountersPerVerificationReport = 100
        ' Enabling for Improved handling of absolute value counters:
        extendedCountObject = True
            rankBusyHourFunctionality = tpUtilities.checkBusyHourFunctionality(TechPackTPIde, tpConn)

        Dim ExtendedUnvList() As String
            Try
                extendedUnv = tpUtilities.readSingleValue("SELECT UNIVERSEEXTENSION || '=' || UNIVERSEEXTENSIONNAME as UNIVERSEEXTENSION FROM UNIVERSENAME WHERE VERSIONID = ", tpConn, dbCommand, dbReader, TechPackTPIde)
            If extendedUnv Is Nothing Then
                extendedUnv = ""
            End If
            ExtendedUnvList = extendedUnv.Split(",")
        Catch ex As Exception
            Console.WriteLine(ex.ToString)
        End Try

        UniverseName = "TP " & Description & " " & TechPackType
        UniverseDescription = Description & " " & TechPackType & " " & Chr(10) & "Version: b" & TPVersion & " " & Chr(10) & _
        "Vendor releases: " & VendorRelease & " " & Chr(10) & _
        "Product: " & ProductNumber & " " & TPReleaseVersion

        If UniverseName.Length > 128 Then
                Trace.WriteLine("Universe name '" & UniverseName & "' exceeds maximum of 128 characters.")
        End If

            ' Get counters:
        all_cnts = New CountersTPIde
            all_cnts.getCounters(DefaultCounterMaxAmount, tpConn, dbCommand, dbReader, TechPackTPIde)

            ' Get counter keys:
        all_cnt_keys = New CounterKeysTPIde
            all_cnt_keys.getCounterKeys(DefaultKeyMaxAmount, tpConn, dbCommand, dbReader, TechPackTPIde)

            ' Get public keys:
        pub_keys = New PublicKeysTPIde
            pub_keys.getPublicKeys(baseConn, dbCommand, dbReader, BaseTechPackTPIde)

            ' Get full list of measurement types:
        original_mts = New MeasurementTypesTPIde
            original_mts.getMeasurements(TechPackName, tpConn, dbCommand, dbReader, all_cnts, all_cnt_keys, pub_keys, TechPackTPIde)

            ' Create new UniverseMeasurementsTPIde for the current universe:
            UnvMts = New UniverseMeasurementsTPIde(TechPackName, tpConn, baseConn, dbCommand, dbReader, original_mts, TechPackTPIde, _
                                                   BaseTechPackTPIde, extendedCountObject, rankBusyHourFunctionality, TPVersion)
            UnvMts.createUniverseMeasurements(ExtendedUnvList, original_mts)

            ' Get list of classes:
            univ_clss = getUniverseClasses(original_mts)

            ' Get report objects (from Verification Objects tab in IDE):
        repobjs = New ReportObjectsTPIde
            repobjs.getReportObjects(tpConn, dbCommand, dbReader, TechPackTPIde)

            ' Get report objects (from Verification Objects tab in IDE):
        repconds = New ReportConditionsTPIde
            repconds.getReportConditions(tpConn, dbCommand, dbReader, TechPackTPIde)
        Catch ex As Exception
            Console.WriteLine("Error initializing classes: " & ex.ToString())
            Trace.WriteLine("Error initializing classes: " & ex.ToString())
            Return 1
        End Try

        Return 0
    End Function

    ''
    'Gets the list of classes.
    '@param     fullListOfMeasTypes The full list of measurement types.
    '@returns   univ_clss           An ArrayList of the universe classes (UnivClassesTPIde object).
    Public Function getUniverseClasses(ByRef fullListOfMeasTypes As MeasurementTypesTPIde) As UnivClassesTPIde
        Dim univ_clss As UnivClassesTPIde = New UnivClassesTPIde
        Dim success As Boolean = False

        success = univ_clss.getClasses(baseConn, dbCommand, dbReader, BaseTechPackTPIde)
        success = univ_clss.getClasses(tpConn, dbCommand, dbReader, TechPackTPIde)
        ' Get the Rank busy hour classes.
        If (rankBusyHourFunctionality = True) Then
            success = univ_clss.getRankingBusyHourClasses(fullListOfMeasTypes)
        End If

        If success = False Then
            Throw New Exception("Error getting tech pack classes")
        End If

        Return univ_clss
    End Function

    'Sub VerifReports_makeVerificationReport_CM(ByRef BoApp As busobj.Application, ByRef OutputDir As String, _
    '                                           ByRef mts As MeasurementTypesTPIde, ByRef UniverseExtension As String, ByRef BOVersion As String)

    '    Dim Doc As busobj.Document
    '    Dim unvObj As busobj.Universe
    '    Dim claObj As Designer.Class
    '    Dim claObj2 As Designer.Class
    '    Dim claObj3 As Designer.Class
    '    Dim claObj4 As Designer.Class
    '    Dim dp As busobj.DataProvider
    '    Dim dp2 As busobj.DataProvider
    '    Dim rep As busobj.Report
    '    Dim RepName As String
    '    Dim numberOfCounters As Integer
    '    Dim numberOfReports As Integer
    '    Dim reportCount As Integer
    '    Dim fromCounter As Integer

    '    'Make Total Verification reports
    '    For mt_count = 1 To mts.Count
    '        mt = mts.Item(mt_count)
    '        If mt.MeasurementTypeID <> "" Then
    '            numberOfReports = 0
    '            fromCounter = 0
    '            numberOfCounters = mt.Counters.Count
    '            If numberOfCounters > CountersPerVerificationReport Then
    '                'calculate number of required reports
    '                numberOfReports = Math.Ceiling(numberOfCounters / CountersPerVerificationReport)
    '            Else
    '                numberOfReports = 1
    '            End If

    '            For reportCount = 1 To numberOfReports
    '                If reportCount = 1 Then
    '                    fromCounter = 1
    '                Else
    '                    fromCounter = (reportCount - 1) * CountersPerVerificationReport + 1
    '                End If
    '                Doc = BoApp.Documents.Add 'Add new document

    '                dp = loadDataProvider(BoApp, Doc, "CM", UniverseExtension, BOVersion)
    '                If dp Is Nothing Then
    '                    Exit Sub
    '                End If
    '                unvObj = dp.Universe 'Set universe
    '                Try
    '                    claObj = VerifReports_ClassObject(unvObj, mt.TypeName, "Parameters", "", mts)  'Get measurement objects
    '                    claObj2 = VerifReports_ClassObject(claObj, mt.TypeName, "", mts)  'Get measurement objects
    '                Catch ex As Exception
    '                    Trace.WriteLine("Couldn't find class object for " & mt.TypeName & ", exiting report generation for this measurement type")
    '                    Exit For
    '                End Try

    '                VerifReports_AddObjects(dp, mt.TypeName, "TOTAL_RAW") 'Add objects
    '                VerifReports_AddClassKeyObjects(dp, claObj2, excludedKeyObjects, mt, hiddenObjects, updatedObjects) 'Add measurement keys
    '                VerifReports_AddCMClassObjects(dp, claObj, mt.TypeName, mt.Counters) 'Add measurement counters
    '                VerifReports_AddConditions(Doc, dp, mt.TypeName, "TOPOLOGY") 'Add topology conditions
    '                VerifReports_AddKeyConditions(Doc, dp, mt.TypeName, "KEYTOPOLOGY", "") 'Add topology conditions
    '                VerifReports_AddConditions(Doc, dp, mt.TypeName, "TOTAL_RAW") 'Add time conditions
    '                Trace.WriteLine("Unloading data provider for CM RAW table")
    '                unloadDataProvider("CM", dp, "CM TOTAL_RAW", mt.TypeName)

    '                'Add new report
    '                ' RepName = "Verification_" & claObj.Name & "_" & UniverseExtension
    '                RepName = ""
    '                RepName = "Verification_" & claObj.Name
    '                If (UniverseExtension <> "") Then
    '                    RepName = RepName & "_" & UniverseExtension
    '                End If
    '                If numberOfReports > 1 Then
    '                    RepName = RepName & "_" & reportCount
    '                End If

    '                Try
    '                    rep = Doc.Reports.CreateQuickReport(dp.Name)
    '                    If (BOVersion = "XI") Then
    '                        applyReportSettings(Doc, rep, "Verification_CM_Template_XI.ret", RepName)
    '                    Else
    '                        applyReportSettings(Doc, rep, "Verification_CM_Template.ret", RepName)
    '                    End If
    '                    VerifReports_BuildRawReportTables(Doc, rep)
    '                    VerifReports_FormatColumns(Doc, mt.MeasurementTypeID, "RAW", mt.Counters)
    '                    SaveReport(Doc, OutputDir, RepName)
    '                Catch ex As Exception
    '                    Trace.WriteLine("Report Create Error for '" & RepName & "'.")
    '                    Trace.WriteLine("Report Create Exception" & ex.ToString)
    '                    SaveReport(Doc, OutputDir, RepName)
    '                End Try
    '            Next reportCount
    '        End If
    '    Next mt_count

    'End Sub
    'Sub SaveReport(ByRef Doc As busobj.Document, ByRef OutputDir As String, ByRef RepName As String)
    '    Dim dp As busobj.DataProvider
    '    Dim Location As String

    '    If OutputDir <> "" Then
    '        Location = OutputDir & "\"
    '    End If
    '    Try
    '        If OutputDir <> "" Then
    '            Doc.SaveAs(Location & RepName, True)
    '        Else
    '            Doc.SaveAs(RepName, True)
    '        End If
    '        Doc.Close()
    '        Trace.WriteLine("Saved report '" & RepName & ".rep' to '" & Location & "'")
    '        Console.WriteLine("Saved report '" & RepName & ".rep' to '" & Location & "'")
    '        totalReportsCreated = totalReportsCreated + 1
    '    Catch ex As Exception
    '        Trace.WriteLine("Report '" & RepName & "' saving to '" & Location & "' failed.")
    '        Trace.WriteLine("Data provider exception: " & ex.ToString)
    '    End Try
    'End Sub

    'Sub unloadDataProvider(ByRef Name As String, ByRef data_prov As busobj.DataProvider, ByVal table As String, ByVal mt As String)
    '    Try
    '        data_prov.Unload()
    '        data_prov.IsRefreshable = True
    '        data_prov.Name = Name
    '        'Console.WriteLine(data_prov.SQL)
    '        If (data_prov.SQL = "" OrElse data_prov Is Nothing) Then
    '            Throw New Exception("No SQL generated for report. Report generation failed.")
    '        End If
    '    Catch ex As Exception
    '        Dim errortext As String
    '        errortext = "Error in SQL for " & table & ": " & "in " & mt
    '        Trace.WriteLine(errortext & ":" & ex.ToString)
    '        Console.WriteLine(errortext)
    '        ' Exit Sub
    '    End Try
    'End Sub
    'Sub applyReportSettings(ByRef Doc As busobj.Document, ByRef rep As busobj.Report, ByRef template As String, ByRef RepName As String)
    '    Try
    '        rep.ApplyTemplate(template)
    '        Doc.DocumentVariables("Report Title").Formula = RepName ' If you are making a template, don't change the formula of Report Title, to anything but
    '        ' "Report Title". Otherwise BO will create a bopy of the variable, and you won't be able to set the one you want - the original Report Title.
    '        rep.Name = RepName
    '        Doc.Title() = RepName
    '    Catch ex As Exception
    '        Trace.WriteLine("Verification report '" & RepName & "' with template '" & template & "' naming failed.")
    '        Trace.WriteLine("Verification report exception:" & ex.ToString)
    '        Exit Sub
    '    End Try
    'End Sub

    'Sub VerifReports_makeVerificationReport_Day(ByRef BoApp As busobj.Application, ByRef OutputDir As String, _
    '                                            ByRef check As Boolean, ByRef mts As MeasurementTypesTPIde, ByRef UniverseExtension As String, _
    '                                            ByRef BoVersion As String)

    '    Dim Doc As busobj.Document
    '    Dim unvObj As busobj.Universe
    '    Dim claObj As Designer.Class
    '    Dim claObj2 As Designer.Class
    '    Dim claObj3 As Designer.Class
    '    Dim claObj4 As Designer.Class
    '    Dim dp As busobj.DataProvider
    '    Dim dp2 As busobj.DataProvider
    '    Dim rep As busobj.Report
    '    Dim RepName As String
    '    Dim count As Integer
    '    Dim CreateReport As Boolean
    '    Dim numberOfCounters As Integer
    '    Dim numberOfReports As Integer
    '    Dim reportCount As Integer
    '    Dim fromCounter As Integer

    '    'Make Total Verification reports
    '    For mt_count = 1 To mts.Count
    '        mt = mts.Item(mt_count)
    '        If mt.MeasurementTypeID <> "" And mt.DayAggregation = True Then
    '            If check = False Then
    '                CreateReport = True
    '            End If
    '            If check = True Then
    '                Dim makeRep As MsgBoxResult
    '                makeRep = tpUtilities.displayMessageBox("Do you want to create DAY Total Verification report for " & mt.TypeName _
    '                                            & "? Press Cancel for skip rest reports of this type.", MsgBoxStyle.YesNoCancel, "Day report for " & mt.TypeName)
    '                If makeRep = MsgBoxResult.Yes Then
    '                    CreateReport = True
    '                ElseIf makeRep = MsgBoxResult.Cancel Then
    '                    Exit Sub
    '                Else
    '                    CreateReport = False
    '                End If
    '            End If

    '            numberOfReports = 0
    '            fromCounter = 0
    '            numberOfCounters = mt.Counters.Count
    '            If numberOfCounters > CountersPerVerificationReport Then
    '                'calculate number of required reports
    '                numberOfReports = Math.Ceiling(numberOfCounters / CountersPerVerificationReport)
    '            Else
    '                numberOfReports = 1
    '            End If

    '            For reportCount = 1 To numberOfReports
    '                If reportCount = 1 Then
    '                    fromCounter = 1
    '                Else
    '                    fromCounter = (reportCount - 1) * CountersPerVerificationReport + 1
    '                End If
    '                'begin create report
    '                If CreateReport = True Then
    '                    Doc = BoApp.Documents.Add 'Add new document
    '                    dp = loadDataProvider(BoApp, Doc, "RAW", UniverseExtension, BoVersion)
    '                    If dp Is Nothing Then
    '                        Exit Sub
    '                    End If
    '                    unvObj = dp.Universe 'Set universe
    '                    Try
    '                        claObj = VerifReports_ClassObject(unvObj, mt.TypeName, "Counters", "", mts) 'Get measurement objects
    '                        If claObj Is Nothing Then
    '                            Trace.WriteLine("Couldn't find class object for " & mt.TypeName & ", exiting report generation for this measurement type")
    '                            Exit For
    '                        End If

    '                        claObj2 = VerifReports_ClassObject(claObj, mt.TypeName, "", mts) 'Get measurement objects
    '                        If claObj2 Is Nothing Then
    '                            Trace.WriteLine("Couldn't find class object for " & mt.TypeName & ", exiting report generation for this measurement type")
    '                            Exit For
    '                        End If

    '                        VerifReports_AddObjects(dp, mt.TypeName, "TOTAL_RAW") 'Add objects
    '                        VerifReports_AddClassKeyObjects(dp, claObj2, excludedKeyObjects, mt, hiddenObjects, updatedObjects) 'Add measurement keys
    '                        VerifReports_AddClassObjects(dp, claObj, mt.TypeName, mt.Counters, "", False, fromCounter, CountersPerVerificationReport) 'Add measurement counters
    '                        VerifReports_AddConditions(Doc, dp, mt.TypeName, "TOPOLOGY") 'Add topology conditions
    '                        VerifReports_AddKeyConditions(Doc, dp, mt.TypeName, "KEYTOPOLOGY", "") 'Add topology conditions
    '                        VerifReports_AddConditions(Doc, dp, mt.TypeName, "TOTAL_RAW") 'Add time conditions
    '                        Trace.WriteLine("Unloading data provider for Day RAW table")
    '                        unloadDataProvider("RAW", dp, "Day RAW table", mt.TypeName)
    '                    Catch ex As Exception
    '                        Trace.WriteLine("Error generating RAW table for Day report for " & mt.TypeName & ", exiting report generation for this measurement type")
    '                        Exit For
    '                    End Try

    '                    dp2 = loadDataProvider(BoApp, Doc, "DAY", UniverseExtension, BoVersion)
    '                    If dp2 Is Nothing Then
    '                        Exit Sub
    '                    End If
    '                    VerifReports_AddObjects(dp2, mt.TypeName, "TOTAL_DAY") 'Add objects
    '                    VerifReports_AddClassKeyObjects(dp2, claObj2, excludedKeyObjects, mt, hiddenObjects, updatedObjects) 'Add measurement keys
    '                    VerifReports_AddClassObjects(dp2, claObj, mt.TypeName, mt.Counters, "", False, fromCounter, CountersPerVerificationReport) 'Add measurement counters
    '                    VerifReports_AddConditions(Doc, dp2, mt.TypeName, "TOPOLOGY") 'Add topology conditions
    '                    VerifReports_AddKeyConditions(Doc, dp2, mt.TypeName, "KEYTOPOLOGY", "") 'Add topology conditions
    '                    VerifReports_AddConditions(Doc, dp2, mt.TypeName, "TOTAL_DAY") 'Add time conditions
    '                    Trace.WriteLine("Unloading data provider for Day DAY table")
    '                    unloadDataProvider("DAY", dp2, "Day DAY table", mt.TypeName)

    '                    'Add new report (& ".txt"  other file extension than rep)
    '                    RepName = ""
    '                    RepName = "Verification_" & claObj.Name
    '                    If (UniverseExtension <> "") Then
    '                        RepName = RepName & "_" & UniverseExtension
    '                    End If
    '                    If numberOfReports > 1 Then
    '                        RepName = RepName & "_" & reportCount
    '                    End If

    '                    Try
    '                        rep = Doc.Reports.CreateQuickReport(dp.Name)
    '                        If (BoVersion = "XI") Then
    '                            applyReportSettings(Doc, rep, "Verification_Template_XI.ret", RepName)
    '                        Else
    '                            applyReportSettings(Doc, rep, "Verification_Template.ret", RepName)
    '                        End If
    '                        VerifReports_BuildRawReportTables(Doc, rep)
    '                        VerifReports_BuildTotalReportTables(Doc, rep, dp2) 'Fill total report tables
    '                        VerifReports_FormatColumns(Doc, mt.TypeName, "RAW", mt.Counters)
    '                        VerifReports_FormatColumns(Doc, mt.TypeName, "DAY", mt.Counters)
    '                        SaveReport(Doc, OutputDir, RepName)
    '                    Catch ex As Exception
    '                        Trace.WriteLine("Report Create Error for '" & RepName & "'. Check report object and conditions for levels TOTAL_RAW, TOTAL_DAY, TOPOLOGY and KEYTOPOLOGY")
    '                        Trace.WriteLine("Report Create Exception" & ex.ToString)
    '                        SaveReport(Doc, OutputDir, RepName)
    '                    End Try
    '                End If
    '                'end create report
    '            Next reportCount
    '        End If
    '    Next mt_count

    'End Sub
    'Sub VerifReports_makeVerificationReport_Count(ByRef BoApp As busobj.Application, ByRef OutputDir As String, ByRef check As Boolean, ByRef mts As MeasurementTypesTPIde, ByRef UniverseExtension As String, ByRef BoVersion As String)

    '    Dim Doc As busobj.Document
    '    Dim unvObj As busobj.Universe
    '    Dim claObj As Designer.Class
    '    Dim claObj2 As Designer.Class
    '    Dim claObj3 As Designer.Class
    '    Dim claObj4 As Designer.Class
    '    Dim dp As busobj.DataProvider
    '    Dim dp2 As busobj.DataProvider
    '    Dim rep As busobj.Report
    '    Dim RepName As String
    '    Dim CreateReport As Boolean
    '    Dim numberOfCounters As Integer
    '    Dim numberOfReports As Integer
    '    Dim reportCount As Integer
    '    Dim fromCounter As Integer

    '    For mt_count = 1 To mts.Count
    '        mt = mts.Item(mt_count)
    '        If mt.MeasurementTypeID <> "" And mt.CreateCountTable = True Then

    '            If check = False Then
    '                CreateReport = True
    '            End If
    '            If check = True Then
    '                Dim makeRep As MsgBoxResult
    '                makeRep = tpUtilities.displayMessageBox("Do you want to create COUNT Total Verification report for " & mt.TypeName _
    '                                 & "? Press Cancel for skip rest reports of this type.", MsgBoxStyle.YesNoCancel, "Count report for " & mt.TypeName)
    '                If makeRep = MsgBoxResult.Yes Then
    '                    CreateReport = True
    '                ElseIf makeRep = MsgBoxResult.Cancel Then
    '                    Exit Sub
    '                Else
    '                    CreateReport = False
    '                End If
    '            End If

    '            numberOfReports = 0
    '            fromCounter = 0
    '            numberOfCounters = mt.Counters.Count
    '            If numberOfCounters > CountersPerVerificationReport Then
    '                'calculate number of required reports
    '                numberOfReports = Math.Ceiling(numberOfCounters / CountersPerVerificationReport)
    '            Else
    '                numberOfReports = 1
    '            End If

    '            For reportCount = 1 To numberOfReports
    '                If reportCount = 1 Then
    '                    fromCounter = 1
    '                Else
    '                    fromCounter = (reportCount - 1) * CountersPerVerificationReport + 1
    '                End If
    '                If CreateReport = True Then
    '                    Doc = BoApp.Documents.Add 'Add new document
    '                    dp = loadDataProvider(BoApp, Doc, "RAW", UniverseExtension, BoVersion)

    '                    If dp Is Nothing Then
    '                        Exit Sub
    '                    End If
    '                    unvObj = dp.Universe 'Set universe
    '                    Try
    '                        claObj3 = VerifReports_ClassObject(unvObj, mt.TypeName, "Counters", "_RAW", mts) 'Get measurement objects
    '                    Catch ex As Exception
    '                        Trace.WriteLine("Couldn't find class object for " & mt.TypeName & ", exiting report generation for this measurement type")
    '                        Exit For
    '                    End Try
    '                    If claObj3 Is Nothing Then
    '                        Trace.WriteLine("Couldn't find class object for " & mt.TypeName & ", exiting report generation for this measurement type")
    '                        Exit For
    '                    End If

    '                    Try
    '                        claObj4 = VerifReports_ClassObject(claObj3, mt.TypeName, "_RAW", mts) 'Get measurement objects
    '                    Catch ex As Exception
    '                        Trace.WriteLine("Couldn't find class object for " & mt.TypeName & ", exiting report generation for this measurement type")
    '                        Exit For
    '                    End Try

    '                    If claObj4 Is Nothing Then
    '                        Trace.WriteLine("Couldn't find class object for " & mt.TypeName & ", exiting report generation for this measurement type")
    '                        Exit For
    '                    End If

    '                    VerifReports_AddObjects(dp, mt.TypeName, "TOTAL_RAW") 'Add objects
    '                    VerifReports_AddClassKeyObjects(dp, claObj4, excludedKeyObjects, mt, hiddenObjects, updatedObjects) 'Add measurement keys
    '                    VerifReports_AddClassObjects(dp, claObj3, mt.TypeName, mt.Counters, "_RAW", True, fromCounter, CountersPerVerificationReport) 'Add measurement counters
    '                    VerifReports_AddConditions(Doc, dp, mt.TypeName, "TOPOLOGY") 'Add topology conditions
    '                    VerifReports_AddKeyConditions(Doc, dp, mt.TypeName, "KEYTOPOLOGY", "_RAW") 'Add topology conditions
    '                    VerifReports_AddConditions(Doc, dp, mt.TypeName, "TOTAL_RAW") 'Add time conditions
    '                    Trace.WriteLine("Unloading data provider for Count RAW table")
    '                    unloadDataProvider("RAW", dp, "Count RAW table", mt.TypeName)

    '                    dp2 = loadDataProvider(BoApp, Doc, "DAY", UniverseExtension, BoVersion)
    '                    If dp2 Is Nothing Then
    '                        Exit Sub
    '                    End If
    '                    Try
    '                        claObj = VerifReports_ClassObject(unvObj, mt.TypeName, "Counters", "", mts) 'Get measurement objects
    '                        claObj2 = VerifReports_ClassObject(claObj, mt.TypeName, "", mts) 'Get measurement objects
    '                    Catch ex As Exception
    '                        Trace.WriteLine("Couldn't find class object for " & mt.TypeName & ", exiting report generation for this measurement type")
    '                        Exit For
    '                    End Try

    '                    VerifReports_AddObjects(dp2, mt.TypeName, "TOTAL_RAW") 'Add objects
    '                    VerifReports_AddClassKeyObjects(dp2, claObj2, excludedKeyObjects, mt, hiddenObjects, updatedObjects) 'Add measurement keys
    '                    VerifReports_AddClassObjects(dp2, claObj, mt.TypeName, mt.Counters, "", True, fromCounter, CountersPerVerificationReport) 'Add measurement counters
    '                    Trace.WriteLine("Adding topology conditions for Count DAY table")
    '                    VerifReports_AddConditions(Doc, dp2, mt.TypeName, "TOPOLOGY") 'Add topology conditions
    '                    Trace.WriteLine("Adding key topology conditions for Count DAY table")
    '                    VerifReports_AddKeyConditions(Doc, dp2, mt.TypeName, "KEYTOPOLOGY", "") 'Add topology conditions
    '                    Trace.WriteLine("Adding time/TOTAL_RAW conditions for Count DAY table")
    '                    VerifReports_AddConditions(Doc, dp2, mt.TypeName, "TOTAL_RAW") 'Add time conditions
    '                    Trace.WriteLine("Unloading data provider for Count DAY table")
    '                    unloadDataProvider("DAY", dp2, "Count DAY table", mt.TypeName)

    '                    'RepName = "Verification_" & claObj.Name & "_COUNT"
    '                    'Modification for TR HK96112 - Start
    '                    RepName = ""
    '                    RepName = "Verification_" & claObj.Name & "_COUNT"
    '                    If (UniverseExtension <> "") Then
    '                        RepName = RepName & "_" & UniverseExtension
    '                    End If
    '                    If numberOfReports > 1 Then
    '                        RepName = RepName & "_" & reportCount
    '                    End If

    '                    'Modification for TR HK96112 - End
    '                    Try
    '                        rep = Doc.Reports.CreateQuickReport(dp.Name)
    '                        If (BoVersion = "XI") Then
    '                            applyReportSettings(Doc, rep, "Verification_Count_Template_XI.ret", RepName)
    '                        Else
    '                            applyReportSettings(Doc, rep, "Verification_Count_Template.ret", RepName)
    '                        End If
    '                        VerifReports_BuildTotalReportTables(Doc, rep, dp2) 'Fill total report tables
    '                        VerifReports_FormatColumns(Doc, mt.TypeName, "RAW", mt.Counters)
    '                        VerifReports_FormatColumns(Doc, mt.TypeName, "DAY", mt.Counters)
    '                        SaveReport(Doc, OutputDir, RepName)
    '                    Catch ex As Exception
    '                        Trace.WriteLine("Report Create Error for '" & RepName & "'.")
    '                        Trace.WriteLine("Report Create Error for '" & RepName & "'. Check report object and conditions for levels TOTAL_RAW, TOPOLOGY and KEYTOPOLOGY")
    '                        Trace.WriteLine("Report Create Exception" & ex.ToString)
    '                        SaveReport(Doc, OutputDir, RepName)
    '                    End Try
    '                End If
    '            Next reportCount
    '        End If
    '    Next mt_count

    'End SubFunction loadDataProvider(ByRef BoApp As busobj.Application, ByRef Doc As busobj.Document, ByRef Name As String, ByRef UniverseExtension As String, ByRef BoVersion As String) As busobj.DataProvider
    '    Dim data_prov As busobj.DataProvider
    '    Dim Univ As busobj.Universe
    '    Dim UniverseFolder As String
    '    UniverseFolder = ""
    '    Try
    '        'For Each Univ In BoApp.Universes
    '        'Try
    '        'If Univ.LongName = UniverseName Then
    '        'UniverseFolder = Univ.DomainName
    '        'Exit For
    '        'End If
    '        'Catch ex As Exception
    '        'Trace.WriteLine("Data provider exception: " & ex.ToString)
    '        'Return Nothing
    '        'End Try
    '        'Next
    '        If (UniverseExtension = "" And BoVersion = "6.5") Then
    '            data_prov = Doc.DataProviders.AddQueryTechnique(UniverseName, "Universe")
    '        ElseIf (UniverseExtension = "" And BoVersion = "XI") Then
    '            data_prov = Doc.DataProviders.AddQueryTechnique(UniverseName, "/ENIQ")
    '        ElseIf (UniverseExtension <> "" And BoVersion = "XI") Then
    '            data_prov = Doc.DataProviders.AddQueryTechnique(UniverseName & " " & UniverseExtension, "/ENIQ")
    '        ElseIf (UniverseExtension <> "" And BoVersion = "6.5") Then
    '            data_prov = Doc.DataProviders.AddQueryTechnique(UniverseName & " " & UniverseExtension, "Universe")
    '        End If

    '        data_prov.IsRefreshable = False
    '        'dp.Name = Name
    '        data_prov.Load()
    '    Catch ex As Exception
    '        Trace.WriteLine("Data provider creation from universe '" & UniverseName & "' with extension '" & UniverseExtension & "' failed.")
    '        Trace.WriteLine("Data provider exception: " & ex.ToString)
    '        Return Nothing
    '    End Try
    '    Return data_prov
    'End Sub
    'Sub VerifReports_makeVerificationReport_DayBH(ByRef BoApp As busobj.Application, ByRef OutputDir As String, ByRef check As Boolean, _
    '                                              ByRef mts As MeasurementTypesTPIde, ByRef UniverseExtension As String, ByRef BoVersion As String)

    '    Dim Doc As busobj.Document
    '    Dim unvObj As busobj.Universe
    '    Dim claObj As Designer.Class
    '    Dim claObj2 As Designer.Class
    '    Dim claObj3 As Designer.Class
    '    Dim claObj4 As Designer.Class
    '    Dim dp As busobj.DataProvider
    '    Dim dp2 As busobj.DataProvider
    '    Dim rep As busobj.Report
    '    Dim RepName As String
    '    Dim CreateReport As Boolean
    '    Dim numberOfCounters As Integer
    '    Dim numberOfReports As Integer
    '    Dim reportCount As Integer
    '    Dim fromCounter As Integer

    '    For mt_count = 1 To mts.Count
    '        mt = mts.Item(mt_count)
    '        If mt.MeasurementTypeID <> "" And mt.ObjectBusyHours <> "" And mt.RankTable = False Then
    '            If check = False Then
    '                CreateReport = True
    '            End If
    '            If check = True Then
    '                Dim makeRep As MsgBoxResult
    '                makeRep = tpUtilities.displayMessageBox("Do you want to create DAYBH Busy Hour Verification report for " _
    '                                            & mt.TypeName & "? Press Cancel for skip rest reports of this type.", _
    '                                            MsgBoxStyle.YesNoCancel, "DAYBH report for " & mt.TypeName)
    '                If makeRep = MsgBoxResult.Yes Then
    '                    CreateReport = True
    '                ElseIf makeRep = MsgBoxResult.Cancel Then
    '                    Exit Sub
    '                Else
    '                    CreateReport = False
    '                End If
    '            End If

    '            numberOfReports = 0
    '            fromCounter = 0
    '            numberOfCounters = mt.Counters.Count
    '            If numberOfCounters > CountersPerVerificationReport Then
    '                'calculate number of required reports
    '                numberOfReports = Math.Ceiling(numberOfCounters / CountersPerVerificationReport)
    '            Else
    '                numberOfReports = 1
    '            End If

    '            For reportCount = 1 To numberOfReports
    '                If reportCount = 1 Then
    '                    fromCounter = 1
    '                Else
    '                    fromCounter = (reportCount - 1) * CountersPerVerificationReport + 1
    '                End If
    '                If CreateReport = True Then
    '                    Doc = BoApp.Documents.Add 'Add new document

    '                    dp = loadDataProvider(BoApp, Doc, "RAW", UniverseExtension, BoVersion)
    '                    If dp Is Nothing Then
    '                        Exit Sub
    '                    End If
    '                    unvObj = dp.Universe 'Set universe

    '                    Try
    '                        claObj = VerifReports_ClassObject(unvObj, mt.TypeName, "Counters", "", mts) 'Get measurement objects
    '                        claObj2 = VerifReports_ClassObject(claObj, mt.TypeName, "", mts) 'Get measurement objects
    '                    Catch ex As Exception
    '                        Trace.WriteLine("Couldn't find class object for " & mt.TypeName & ", exiting report generation for this measurement type")
    '                        Exit For
    '                    End Try

    '                    If claObj Is Nothing Then
    '                        Trace.WriteLine("Couldn't find class object for " & mt.TypeName & ", exiting report generation for this measurement type")
    '                        Exit For
    '                    End If

    '                    claObj2 = VerifReports_ClassObject(claObj, mt.TypeName, "", mts) 'Get measurement objects
    '                    If claObj Is Nothing Then
    '                        Trace.WriteLine("Couldn't find class object for " & mt.TypeName & ", exiting report generation for this measurement type")
    '                        Exit For
    '                    End If

    '                    VerifReports_AddObjects(dp, mt.TypeName, "DAYBH_RAW") 'Add objects
    '                    VerifReports_AddClassKeyObjects(dp, claObj2, excludedKeyObjects, mt, hiddenObjects, updatedObjects) 'Add measurement keys
    '                    VerifReports_AddClassObjects(dp, claObj, mt.TypeName, mt.Counters, "", False, fromCounter, CountersPerVerificationReport) 'Add measurement counters
    '                    VerifReports_AddObjects(dp, mt.TypeName, "DAYBH_RAW_BH") 'Add objects
    '                    VerifReports_AddConditions(Doc, dp, mt.TypeName, "TOPOLOGY") 'Add topology conditions
    '                    VerifReports_AddKeyConditions(Doc, dp, mt.TypeName, "KEYTOPOLOGY", "") 'Add topology conditions
    '                    VerifReports_AddConditions(Doc, dp, mt.TypeName, "DAYBH_RAW") 'Add time conditions
    '                    Trace.WriteLine("Unloading data provider for DAYBH RAW table")
    '                    unloadDataProvider("RAW", dp, "DAYBH RAW table", mt.TypeName)

    '                    dp2 = loadDataProvider(BoApp, Doc, "DAY", UniverseExtension, BoVersion)
    '                    If dp2 Is Nothing Then
    '                        Exit Sub
    '                    End If

    '                    Try
    '                        If FullAware = True Then
    '                            claObj = VerifReports_ClassObject(unvObj, mt.TypeName, "Counters", "", mts) 'Get measurement objects
    '                            claObj2 = VerifReports_ClassObject(claObj, mt.TypeName, "", mts) 'Get measurement objects
    '                        Else
    '                            claObj = VerifReports_ClassObject(unvObj, mt.TypeName, "Counters", "_BH", mts) 'Get measurement objects
    '                            claObj2 = VerifReports_ClassObject(claObj, mt.TypeName, "_BH", mts) 'Get measurement objects
    '                        End If
    '                    Catch ex As Exception
    '                        Trace.WriteLine("Couldn't find class object for " & mt.TypeName & ", exiting report generation for this measurement type")
    '                        Exit For
    '                    End Try

    '                    VerifReports_AddObjects(dp2, mt.TypeName, "DAYBH_DAY") 'Add objects
    '                    VerifReports_AddClassKeyObjects(dp2, claObj2, excludedKeyObjects, mt, hiddenObjects, updatedObjects) 'Add measurement keys
    '                    If FullAware = True Then
    '                        VerifReports_AddClassObjects(dp2, claObj, mt.TypeName, mt.Counters, "", False, fromCounter, CountersPerVerificationReport) 'Add measurement counters
    '                    Else
    '                        VerifReports_AddClassBHObjects(dp2, claObj, mt.TypeName, mt.Counters) 'Add measurement counters
    '                    End If
    '                    VerifReports_AddConditions(Doc, dp2, mt.TypeName, "TOPOLOGY") 'Add topology conditions
    '                    If FullAware = True Then
    '                        VerifReports_AddKeyConditions(Doc, dp2, mt.TypeName, "KEYTOPOLOGY", "") 'Add topology conditions
    '                    Else
    '                        VerifReports_AddKeyConditions(Doc, dp2, mt.TypeName, "KEYTOPOLOGY", "_BH") 'Add topology conditions
    '                    End If
    '                    VerifReports_AddConditions(Doc, dp2, mt.TypeName, "DAYBH") 'Add time conditions
    '                    VerifReports_AddConditions(Doc, dp2, mt.TypeName, "DAYBH_DAY") 'Add time conditions
    '                    Trace.WriteLine("Unloading data provider for DAYBH DAY table")
    '                    unloadDataProvider("DAY", dp2, "DAYBH DAY table", mt.TypeName)

    '                    RepName = ""
    '                    RepName = "Verification_" & claObj.Name & "_BH"
    '                    If (UniverseExtension <> "") Then
    '                        RepName = RepName & "_" & UniverseExtension
    '                    End If
    '                    If numberOfReports > 1 Then
    '                        RepName = RepName & "_" & reportCount
    '                    End If

    '                    Try
    '                        rep = Doc.Reports.CreateQuickReport(dp.Name)
    '                        If (BoVersion = "XI") Then
    '                            applyReportSettings(Doc, rep, "Verification_BH_Template_XI.ret", RepName)
    '                        Else
    '                            applyReportSettings(Doc, rep, "Verification_BH_Template.ret", RepName)
    '                        End If
    '                        Trace.WriteLine("Building report tables for DAYBH in " & RepName)
    '                        VerifReports_BuildBHReportTables(Doc, rep, dp2) 'Fill tables
    '                        VerifReports_FormatColumns(Doc, mt.TypeName, "RAW", mt.Counters)
    '                        VerifReports_FormatColumns(Doc, mt.TypeName, "DAY", mt.Counters)
    '                        SaveReport(Doc, OutputDir, RepName)
    '                    Catch ex As Exception
    '                        Trace.WriteLine("Report Create Error for '" & RepName & "'.")
    '                        Trace.WriteLine("Report Create Error for '" & RepName & "'. Check report object and conditions for levels DAYBH_RAW, TOPOLOGY and KEYTOPOLOGY")
    '                        Trace.WriteLine("Report Create Exception" & ex.ToString)
    '                        SaveReport(Doc, OutputDir, RepName)
    '                    End Try

    '                End If
    '            Next reportCount
    '        End If
    '    Next mt_count

    'End Sub
    'Sub VerifReports_makeVerificationReport_ElemBH(ByRef BoApp As busobj.Application, ByRef OutputDir As String, _
    '                                               ByRef check As Boolean, ByRef mts As MeasurementTypesTPIde, ByRef UniverseExtension As String, _
    '                                               ByRef BoVersion As String)

    '    Dim Doc As busobj.Document
    '    Dim unvObj As busobj.Universe
    '    Dim claObj As Designer.Class
    '    Dim claObj2 As Designer.Class
    '    Dim claObj3 As Designer.Class
    '    Dim claObj4 As Designer.Class
    '    Dim dp As busobj.DataProvider
    '    Dim dp2 As busobj.DataProvider
    '    Dim rep As busobj.Report
    '    Dim RepName As String
    '    Dim i As Integer
    '    Dim CreateReport As Boolean
    '    Dim numberOfCounters As Integer
    '    Dim numberOfReports As Integer
    '    Dim reportCount As Integer
    '    Dim fromCounter As Integer

    '    For mt_count = 1 To mts.Count
    '        mt = mts.Item(mt_count)
    '        If mt.MeasurementTypeID <> "" And mt.ElementBusyHours = True And mt.RankTable = False Then
    '            If check = False Then
    '                CreateReport = True
    '            End If
    '            If check = True Then
    '                Dim makeRep As MsgBoxResult
    '                makeRep = tpUtilities.displayMessageBox("Do you want to create ELEMBH Busy Hour Verification report for " & _
    '                                            mt.TypeName & "? Press Cancel for skip rest reports of this type.", _
    '                                            MsgBoxStyle.YesNoCancel, "ELEMBH report for " & mt.TypeName)
    '                If makeRep = MsgBoxResult.Yes Then
    '                    CreateReport = True
    '                ElseIf makeRep = MsgBoxResult.Cancel Then
    '                    Exit Sub
    '                Else
    '                    CreateReport = False
    '                End If
    '            End If

    '            numberOfReports = 0
    '            fromCounter = 0
    '            numberOfCounters = mt.Counters.Count
    '            If numberOfCounters > CountersPerVerificationReport Then
    '                'calculate number of required reports
    '                numberOfReports = Math.Ceiling(numberOfCounters / CountersPerVerificationReport)
    '            Else
    '                numberOfReports = 1
    '            End If

    '            For reportCount = 1 To numberOfReports
    '                If reportCount = 1 Then
    '                    fromCounter = 1
    '                Else
    '                    fromCounter = (reportCount - 1) * CountersPerVerificationReport + 1
    '                End If
    '                If CreateReport = True Then
    '                    Doc = BoApp.Documents.Add 'Add new document

    '                    dp = loadDataProvider(BoApp, Doc, "RAW", UniverseExtension, BoVersion)
    '                    If dp Is Nothing Then
    '                        Exit Sub
    '                    End If
    '                    unvObj = dp.Universe 'Set universe
    '                    Try
    '                        claObj = VerifReports_ClassObject(unvObj, mt.TypeName, "Counters", "", mts) 'Get measurement objects
    '                        claObj2 = VerifReports_ClassObject(claObj, mt.TypeName, "", mts) 'Get measurement objects
    '                    Catch ex As Exception
    '                        Trace.WriteLine("Couldn't find class object for " & mt.TypeName & ", exiting report generation for this measurement type")
    '                        Exit For
    '                    End Try

    '                    VerifReports_AddObjects(dp, mt.TypeName, "ELEM_RAW") 'Add objects
    '                    VerifReports_AddClassObjects(dp, claObj, mt.TypeName, mt.Counters, "", False, fromCounter, CountersPerVerificationReport) 'Add measurement counters
    '                    VerifReports_AddObjects(dp, mt.TypeName, "ELEM_RAW_BH") 'Add objects
    '                    VerifReports_AddConditions(Doc, dp, mt.TypeName, "DAYBH_RAW") 'Add time conditions
    '                    VerifReports_AddConditions(Doc, dp, mt.TypeName, "ELEMBH_RAW") 'Add time conditions
    '                    Trace.WriteLine("Unloading data provider for ELEMBH RAW table")
    '                    unloadDataProvider("RAW", dp, "ELEMBH RAW table", mt.TypeName)

    '                    dp2 = loadDataProvider(BoApp, Doc, "DAY", UniverseExtension, BoVersion)
    '                    If dp2 Is Nothing Then
    '                        Exit Sub
    '                    End If
    '                    VerifReports_AddObjects(dp2, mt.TypeName, "ELEM_DAY") 'Add objects
    '                    VerifReports_AddClassObjects(dp2, claObj, mt.TypeName, mt.Counters, "", False, fromCounter, CountersPerVerificationReport) 'Add measurement counters
    '                    VerifReports_AddConditions(Doc, dp2, mt.TypeName, "DAYBH")
    '                    VerifReports_AddConditions(Doc, dp2, mt.TypeName, "ELEMBH_DAY") 'Add time conditions
    '                    Trace.WriteLine("Unloading data provider for ELEMBH DAY table")
    '                    unloadDataProvider("DAY", dp2, "ELEMBH DAY table", mt.TypeName)

    '                    'Add new report (& ".txt"  other file extension than rep)
    '                    RepName = ""
    '                    RepName = "Verification_" & claObj.Name & "_ELEMBH"
    '                    If (UniverseExtension <> "") Then
    '                        RepName = RepName & "_" & UniverseExtension
    '                    End If
    '                    If numberOfReports > 1 Then
    '                        RepName = RepName & "_" & reportCount
    '                    End If

    '                    Try
    '                        rep = Doc.Reports.CreateQuickReport(dp.Name)
    '                        If (BoVersion = "XI") Then
    '                            applyReportSettings(Doc, rep, "Verification_BH_Template_XI.ret", RepName)
    '                        Else
    '                            applyReportSettings(Doc, rep, "Verification_BH_Template.ret", RepName)
    '                        End If
    '                        Trace.WriteLine("Building report tables for ELEMBH in " & RepName)
    '                        VerifReports_BuildBHReportTables(Doc, rep, dp2) 'Fill tables
    '                        VerifReports_FormatColumns(Doc, mt.TypeName, "RAW", mt.Counters)
    '                        VerifReports_FormatColumns(Doc, mt.TypeName, "DAY", mt.Counters)
    '                        SaveReport(Doc, OutputDir, RepName)
    '                    Catch ex As Exception
    '                        Trace.WriteLine("Report Create Error for '" & RepName & "'.")
    '                        Trace.WriteLine("Report Create Error for '" & RepName & "'. Check report object and conditions for levels ELEM_RAW and DAYBH_RAW")
    '                        Trace.WriteLine("Report Create Exception" & ex.ToString)
    '                        SaveReport(Doc, OutputDir, RepName)
    '                    End Try

    '                End If
    '            Next reportCount
    '        End If
    '    Next mt_count

    'End Sub

    '' 
    'Make Rank BH verification report.
    '@param BoApp               Instance of the business objects Application.
    '@param OutputDir           The directory to save the reports into.
    '@param check               A boolean value. If true, ask the user if they want to create each report.
    '@param mts                 The measurement types
    '@param UniverseExtension   The universe extension.
    '@param BoVersion           ("6.5" or "XI")
    'Public Sub VerifReports_makeVerificationReport_RankBH(ByRef BoApp As busobj.IApplication, ByRef OutputDir As String, _
    '                                           ByRef check As Boolean, ByRef mts As MeasurementTypesTPIde, ByRef UniverseExtension As String, _
    '                                           ByRef BoVersion As String)
    '    Trace.WriteLine(getClassNameAndFunction(GetCurrentMethod.Name) & "Entering")
    '    Dim checkResult As String
    '    Dim targetType As String

    '    ' New mtype variable to avoid mixing it up with mt:
    '    Dim rankMeasType As MeasurementTypesTPIde.MeasurementType
    '    Dim rankMeasurementTypes As ArrayList = New ArrayList()
    '    Dim targetTypes As ArrayList = New ArrayList()
    '    Dim sourceTypes As ArrayList = New ArrayList()

    '    ' Get the rank measurement types:
    '    rankMeasurementTypes = tpUtilities.getRankMeasurementTypes(mts)

    '    For Each rankMeasType In rankMeasurementTypes
    '        ' Only add reports for measurement types with ranking tables:
    '        If rankMeasType.MeasurementTypeID <> "" And rankMeasType.RankTable = True Then
    '            targetTypes = getRankBHTargetTypes(TechPackTPIde, rankMeasType.TypeName)

    '            ' Get the source types:
    '            sourceTypes = getRankBHSourceTypes(TechPackTPIde, rankMeasType.TypeName)

    '            For Each sourceType As String In sourceTypes
    '                ' Add source tables to the list of types if the source type is not already there:
    '                If Not (targetTypes.Contains(sourceType)) Then
    '                    targetTypes.Add(sourceType)
    '                End If
    '            Next

    '            If (targetTypes.Count = 0) Then
    '                Console.WriteLine("No mapped types found for " & rankMeasType.TypeName & ", skipping report generation.")
    '                Trace.WriteLine("No mapped types found for " & rankMeasType.TypeName & ", skipping report generation.")
    '                Continue For
    '            End If
    '            'Go through each of the target types:
    '            For Each targetType In targetTypes
    '                Try
    '                    ' Get the measurement type object for the targetType
    '                    Dim targetMType As MeasurementTypesTPIde.MeasurementType
    '                    targetMType = tpUtilities.getMeasurementTypeByName(targetType, mts)

    '                    ' Check if target measurement type is in the current universe: 
    '                    If (targetMType Is Nothing) Then
    '                        Console.WriteLine("Target measurement type " & targetType & " is not in current universe, report will not be generated.")
    '                        Trace.WriteLine("Target measurement type " & targetType & " is not in current universe, report will not be generated.")
    '                        Continue For
    '                    End If

    '                    If (check = False) Then
    '                        ' Don't check. User clicked Yes to create all reports:
    '                        checkResult = "Yes"
    '                    ElseIf (targetMType.RankTable = False) Then
    '                        ' Don't allow busy hour types as target types:
    '                        checkResult = createReportYesNoCancel(check, targetType, rankMeasType)
    '                    Else
    '                        Continue For
    '                    End If

    '                    If checkResult = "Cancel" Then
    '                        Trace.WriteLine(getClassNameAndFunction(GetCurrentMethod.Name) & "User clicked cancel, exiting rankbh report creation for " _
    '                                        & rankMeasType.TypeName)
    '                        Exit Sub
    '                    ElseIf checkResult = "No" Then
    '                        Continue For
    '                    ElseIf checkResult = "Yes" Then
    '                        ' Skip report generation if the target type is a rankbh type:
    '                        If (targetMType.RankTable = True) Then
    '                            Console.WriteLine("Skipping report generation for " & targetMType.TypeName)
    '                            Trace.WriteLine("Skipping report generation for " & targetMType.TypeName)
    '                            Continue For
    '                        End If
    '                        ' Create the report:
    '                        createRankBusyHourReport(BoApp, BoVersion, rankMeasType, targetMType, targetType, OutputDir, mts, UniverseExtension)
    '                    End If
    '                Catch ex As Exception
    '                    Console.WriteLine("Error generating report for mapped type " & targetType)
    '                    Trace.WriteLine("Error generating report for mapped type " & targetType & ": " & ex.ToString())
    '                End Try
    '            Next targetType
    '        End If 'check for measurement type
    '    Next rankMeasType
    '    Trace.WriteLine(getClassNameAndFunction(GetCurrentMethod.Name) & "Exiting")
    'End Sub

    ''
    'Creates a single rank busy hour report.
    '@param BoApp                   Instance of the business objects Application (interface type IApplication).
    '@param BoVersion               ("6.5" or "XI").
    '@param rankMeasType            The rank measurement type name as a String.
    '@param targetMType             The target measurement type for the busy hour as a MeasurementType.
    '@param targetType              The target type as a String.
    '@param OutputDir               The output directory.
    '@param MeasurementTypesTPIde   The list of measurement types for the current universe.
    'Public Overridable Sub createRankBusyHourReport(ByRef BoApp As busobj.IApplication, ByRef BoVersion As String, _
    '                                                ByRef rankMeasType As MeasurementTypesTPIde.MeasurementType, _
    '                                                ByRef targetMType As MeasurementTypesTPIde.MeasurementType, ByVal targetType As String, _
    '                                                ByVal OutputDir As String, ByRef mts As MeasurementTypesTPIde, ByVal UniverseExtension As String)
    '    'Start creating the report:
    '    Trace.WriteLine(getClassNameAndFunction(GetCurrentMethod.Name) & "Creating Rank busy hour report for " _
    '                    & rankMeasType.TypeName & " based on " & targetType)

    '    Dim Doc As busobj.Document
    '    Dim unvObj As busobj.Universe
    '    'Data providers:
    '    Dim rankDataProvider As busobj.DataProvider
    '    Dim rawDataProvider As busobj.DataProvider
    '    Dim dayDataProvider As busobj.DataProvider
    '    Dim placeholderDataProvider As busobj.DataProvider

    '    ' Set up report name with default value:
    '    Dim reportName As String = "Verification Report"
    '    Dim numberOfCounters As Integer = 0
    '    Dim totalNumberOfReports As Integer = 0
    '    Dim reportNumber As Integer = 1
    '    Dim fromCounter As Integer = 0
    '    fromCounter = 0

    '    numberOfCounters = targetMType.Counters.Count
    '    totalNumberOfReports = getNumberOfReports(numberOfCounters, CountersPerVerificationReport)

    '    For reportNumber = 1 To totalNumberOfReports
    '        fromCounter = getStartingCounter(reportNumber, CountersPerVerificationReport)
    '        'Add a new document for this report:
    '        Doc = BoApp.Documents.Add
    '        Try
    '            ' Create the RANKBH table:
    '            rankDataProvider = createRankTableForRankBHReport(BoApp, Doc, UniverseExtension, BoVersion, rankMeasType, targetMType)
    '            'Set up the universe:
    '            unvObj = rankDataProvider.Universe
    '            ' Create the RAW table:
    '            rawDataProvider = createRawTableForRankBHReport(BoApp, Doc, UniverseExtension, BoVersion, rankMeasType, targetMType, unvObj, fromCounter, mts)

    '            If (rankMeasType.ElementBusyHours = False) Then
    '                ' Create the DAYBH table:
    '                dayDataProvider = createDayBHTableForRankBHReport(BoApp, Doc, UniverseExtension, BoVersion, rankMeasType, targetMType, unvObj, fromCounter, mts)
    '            End If
    '            ' Get the report name:
    '            reportName = getRankBHReportName(reportNumber, totalNumberOfReports, rankMeasType.TypeName, targetType, UniverseExtension)
    '            ' Format the report with the template, and then save it:
    '            formatAndSaveReport(Doc, rankDataProvider, rawDataProvider, dayDataProvider, BoVersion, rankMeasType.TypeName, targetMType, _
    '                                                reportName, OutputDir, rankMeasType.ElementBusyHours)
    '        Catch ex As Exception
    '            Console.WriteLine("Error creating report: " & rankMeasType.TypeName & " based on " & targetType)
    '            Trace.WriteLine("Error creating rank busy hour report: " & rankMeasType.TypeName & " based on " & targetType)
    '        End Try
    '    Next reportNumber
    'End Sub

    '' 
    'Get the BHTARGETTYPE. This has the target measurement types for a place holder:
    '@param TechPackTPIde
    '@param databaseFacade
    '@param rankMeasTypeName
    '@returns ArrayList with rank busy hour target types
    Public Function getRankBHTargetTypes(ByVal TechPackTPIde As String, ByVal rankMeasTypeName As String) As ArrayList
        Dim targetTypes As ArrayList = New ArrayList()
        Dim sqlStatement As String

        sqlStatement = "Select DISTINCT BHTARGETTYPE from BusyhourMapping where VERSIONID = '" & TechPackTPIde & _
        "' AND BHLEVEL = '" & rankMeasTypeName & "' AND BHTYPE LIKE 'PP%'"
        databaseProxy.setupDatabaseReader(sqlStatement, tpConn)
        targetTypes = databaseProxy.readSingleColumnFromDB(sqlStatement, False)
        Return targetTypes
    End Function

    ''
    'Gets the target levels for a busy hour.
    '@param TechPackTPIde
    '@param rankMeasTypeName
    '@param bhTargetType
    '@returns
    Public Function getRankBHTargetLevels(ByVal TechPackTPIde As String, ByVal rankMeasTypeName As String, ByVal bhTargetType As String) As ArrayList
        Dim bhTypes As ArrayList
        Dim sqlStatement As String

        sqlStatement = "Select BHTARGETLEVEL from BusyhourMapping where VERSIONID = '" & TechPackTPIde & _
        "' AND BHLEVEL = '" & rankMeasTypeName & "' AND BHTARGETTYPE = '" & bhTargetType & "' AND BHTYPE LIKE 'PP%'"
        databaseProxy.setupDatabaseReader(sqlStatement, tpConn)
        bhTypes = databaseProxy.readSingleColumnFromDB(sqlStatement, True)
        Return bhTypes
    End Function

    ''
    'Gets the source types for a busy hour.
    '@param TechPackTPIde       
    '@param rankMeasTypeName
    Public Function getRankBHSourceTypes(ByVal TechPackTPIde As String, ByVal rankMeasTypeName As String) As ArrayList
        Dim sourceTables As ArrayList = New ArrayList()
        Dim sourceTypes As ArrayList = New ArrayList()
        Dim sqlStatement As String

        Try
            sqlStatement = "Select DISTINCT TYPENAME from BusyhourSource where VERSIONID = '" & TechPackTPIde & _
            "' AND BHLEVEL = '" & rankMeasTypeName & "' AND BHTYPE LIKE 'PP%'"
            databaseProxy.setupDatabaseReader(sqlStatement, tpConn)
            sourceTables = databaseProxy.readSingleColumnFromDB(sqlStatement, False)

            If (sourceTables.Count = 0) Then
                Trace.WriteLine("No source tables found for : " & rankMeasTypeName)
            End If

            For Each sourceTable As String In sourceTables
                Trace.WriteLine("Checking source table : " & sourceTable)

                If (sourceTable.StartsWith("DIM")) Then
                    ' DIM tables can be added directly:
                    sourceTypes.Add(sourceTable)
                ElseIf (sourceTable.EndsWith("_RAW") Or sourceTable.EndsWith("_COUNT")) Then
                    Dim sourceType As String = ""
                    Dim lastUnderscoreIndex As Integer = sourceTable.LastIndexOf("_")
                    If (lastUnderscoreIndex > 0) Then
                        sourceType = sourceTable.Substring(0, lastUnderscoreIndex)
                        sourceTypes.Add(sourceType)
                        Trace.WriteLine("getRankBHSourceTypes(), found source type for " & TechPackTPIde & ": " & sourceType)
                    End If
                Else
                    Trace.WriteLine("Invalid source table type value read from the database: " & sourceTable)
                End If
            Next
        Catch ex As Exception
            Trace.WriteLine("Error getting the source types for busy hour: " & rankMeasTypeName & ", " & ex.ToString())
        End Try

        Return sourceTypes
    End Function

    ''
    'Creates the rankbh table in the rank busy hour report.
    '@param BoApp
    '@param Doc
    '@param UniverseExtension
    '@param BoVersion
    '@param rankMeasType
    '@param targetMType
    '@returns The busobj.DataProvider object for the Rank busy hour table in the report.
    'Public Function createRankTableForRankBHReport(ByRef BoApp As busobj.Application, ByRef Doc As busobj.Document, _
    '                            ByRef UniverseExtension As String, ByRef BoVersion As String, _
    '                           ByRef rankMeasType As MeasurementTypesTPIde.MeasurementType, _
    '                           ByRef targetMType As MeasurementTypesTPIde.MeasurementType) As busobj.DataProvider
    '    Dim claObj As Designer.Class

    '    Dim unvObj As busobj.Universe
    '    Dim conditionClass As String
    '    Dim conditionName As String
    '    Dim rankDataProvider As busobj.DataProvider

    '    ' Create the RANKBH table:
    '    rankDataProvider = loadDataProvider(BoApp, Doc, "RANKBH", UniverseExtension, BoVersion)
    '    If rankDataProvider Is Nothing Then
    '        Trace.WriteLine(getClassNameAndFunction(GetCurrentMethod.Name) & "Error getting rank data provider")
    '        Throw New Exception(getClassNameAndFunction(GetCurrentMethod.Name) & "Error getting rank data provider")
    '    End If
    '    unvObj = rankDataProvider.Universe

    '    claObj = getBusyHourRankingReportClass(rankMeasType, "Busy Hour", "_RANKBH")
    '    If claObj Is Nothing Then
    '        Trace.WriteLine("Couldn't find class object for " & rankMeasType.TypeName & ", exiting report generation for " & targetMType.TypeName)
    '        Throw New Exception(getClassNameAndFunction(GetCurrentMethod.Name) & "Error getting class object")
    '    End If

    '    addRankBHTableObjects(rankDataProvider, claObj, targetMType)
    '    ' Add topology conditions:
    '    VerifReports_AddConditions(Doc, rankDataProvider, targetMType.TypeName, "TOPOLOGY") 'Add topology conditions

    '    ' If no conditions found for TOPOLOGY, add in element key conditions from busy hour class:
    '    If (rankDataProvider.Queries.Item(1).Conditions.Count = 0) Then
    '        ' Get element key and add as a condition:
    '        Dim keys As CounterKeysTPIde
    '        Dim key As CounterKeysTPIde.CounterKey
    '        keys = rankMeasType.CounterKeys
    '        Dim keyCount As Integer
    '        keyCount = 1

    '        For keyCount = 1 To keys.Count
    '            key = keys.Item(keyCount)
    '            If (key.Element = 1) Then
    '                ' Add a condition for this key:
    '                conditionClass = rankMeasType.TypeName & "_RANKBH"
    '                conditionName = "Select " & key.CounterKeyName
    '                addConditionObject(rankDataProvider, Doc, conditionClass, conditionName)
    '            End If
    '        Next
    '    End If

    '    ' Add condition for the Rank BH type:
    '    conditionClass = rankMeasType.TypeName & "_RANKBH"
    '    conditionName = "Select Busy Hour Type"
    '    addConditionObject(rankDataProvider, Doc, conditionClass, conditionName)

    '    If (rankMeasType.ElementBusyHours = True) Then
    '        ' Add condition for the BH type:
    '        conditionClass = "Element (Busy Hour)"
    '        conditionName = "Select Element Name"
    '        addConditionObject(rankDataProvider, Doc, conditionClass, conditionName)
    '    End If

    '    ' Add condition for the busy hour DATE_ID:
    '    conditionClass = "Selection"
    '    conditionName = "Date Between Date1 and Date2"
    '    addConditionObject(rankDataProvider, Doc, conditionClass, conditionName)

    '    Dim docVariable As busobj.DocumentVariable
    '    docVariable = Doc.DocumentVariables.Add("<Enter formula>", "BHCriteria")
    '    docVariable.Qualification = busobj.BoObjectQualification.boMeasure

    '    unloadDataProvider("RANKBH", rankDataProvider, "Rank Busy Hour table", targetMType.TypeName)

    '    Return rankDataProvider
    'End Function

    '' 
    ' Creates the raw table in the rank busy hour report
    '@param BoApp
    '@param Doc
    '
    '@param UniverseExtension
    '@param BoVersion
    '@param rankMeasType
    '@param targetMType
    '@param unvObj
    '@param fromCounter
    '@param mts
    '@returns The RAW data provider.

    'Public Function createRawTableForRankBHReport(ByRef BoApp As busobj.Application, ByRef Doc As busobj.Document, _
    '                                ByRef UniverseExtension As String, ByRef BoVersion As String, _
    '                               ByRef rankMeasType As MeasurementTypesTPIde.MeasurementType, _
    '                               ByRef targetMType As MeasurementTypesTPIde.MeasurementType, ByRef unvObj As busobj.Universe, _
    '                               ByVal fromCounter As Integer, ByRef mts As MeasurementTypesTPIde) As busobj.DataProvider
    '    Dim claObj As Designer.Class
    '    Dim claObj2 As Designer.Class

    '    Dim rawDataProvider As busobj.DataProvider

    '    rawDataProvider = loadDataProvider(BoApp, Doc, "RAW", UniverseExtension, BoVersion)
    '    If rawDataProvider Is Nothing Then
    '        Trace.WriteLine("Couldn't find class object for " & rankMeasType.TypeName & ", exiting report generation for " & targetMType.TypeName)
    '        Throw New Exception(getClassNameAndFunction(GetCurrentMethod.Name) & "Error getting raw data provider")
    '    End If

    '    ' Get class objects. Class itself, and _Keys class:
    '    claObj = VerifReports_ClassObject(unvObj, targetMType.TypeName, "Counters", "", mts) 'Get measurement objects
    '    If claObj Is Nothing Then
    '        Trace.WriteLine("Couldn't find class object for " & targetMType.TypeName & ", exiting report generation for " & targetMType.TypeName)
    '        Throw New Exception(getClassNameAndFunction(GetCurrentMethod.Name) & "Error getting class object")
    '    End If
    '    claObj2 = VerifReports_ClassObject(claObj, targetMType.TypeName, "", mts) 'Get measurement objects

    '    'If (targetMType.ElementBusyHours = True) Then
    '    ' Add the following raw data:                                                        
    '    'VerifReports_AddObjects(rawDataProvider, targetMType.TypeName, "ELEM_RAW") 'Add objects
    '    'VerifReports_AddClassObjects(rawDataProvider, claObj, targetMType.TypeName, mt.Counters, "", False, fromCounter, CountersPerVerificationReport) 'Add measurement counters
    '    'VerifReports_AddObjects(rawDataProvider, targetMType.TypeName, "ELEM_RAW_BH") 'Add objects                                                                                        
    '    'Else
    '    ' Original:
    '    VerifReports_AddObjects(rawDataProvider, targetMType.TypeName, "DAYBH_RAW") 'Add objects
    '    VerifReports_AddClassKeyObjects(rawDataProvider, claObj2, excludedKeyObjects, mt, hiddenObjects, updatedObjects) 'Add measurement keys
    '    VerifReports_AddClassObjects(rawDataProvider, claObj, targetMType.TypeName, targetMType.Counters, "", False, fromCounter, CountersPerVerificationReport) 'Add measurement counters                                
    '    ' New:
    '    VerifReports_AddObjects(rawDataProvider, mt.TypeName, "DAYBH_RAW_BH")
    '    'End If

    '    VerifReports_AddConditions(Doc, rawDataProvider, targetMType.TypeName, "TOPOLOGY") 'Add topology conditions
    '    VerifReports_AddKeyConditions(Doc, rawDataProvider, targetMType.TypeName, "KEYTOPOLOGY", "") 'Add topology conditions

    '    ' Add Select Date condition:
    '    addConditionObject(rawDataProvider, Doc, "Selection", "Date Between Date1 and Date2")

    '    ' Unload the RAW data provider:
    '    unloadDataProvider("RAW", rawDataProvider, "Rank busy hour, RAW table", targetMType.TypeName)
    '    Return rawDataProvider
    'End Function
    ''
    ' Creates the DAYBH table for the rank busy hour report.
    '@param BoApp
    '@param Doc
    '@param UniverseExtension
    '@param BoVersion
    '@param rankMeasType
    '@param targetMType
    '@param unvObj
    '@param fromCounter
    '@param mts
    '@returns               
    'Public Function createDayBHTableForRankBHReport(ByRef BoApp As busobj.Application, ByRef Doc As busobj.Document, _
    '                            ByRef UniverseExtension As String, ByRef BoVersion As String, _
    '                           ByRef rankMeasType As MeasurementTypesTPIde.MeasurementType, _
    '                           ByRef targetMType As MeasurementTypesTPIde.MeasurementType, ByRef unvObj As busobj.Universe, _
    '                           ByVal fromCounter As Integer, ByRef mts As MeasurementTypesTPIde) As busobj.DataProvider
    '    Dim claObj As Designer.Class
    '    Dim claObj2 As Designer.Class
    '    Dim rankClass As Designer.Class

    '    Dim daybhDataProvider As busobj.DataProvider

    '    ' Load data provider for DAYBH:
    '    daybhDataProvider = loadDataProvider(BoApp, Doc, "DayBH", UniverseExtension, BoVersion)
    '    If daybhDataProvider Is Nothing Then
    '        Trace.WriteLine("Couldn't load data provider for " & rankMeasType.TypeName & ", exiting report generation for " & targetMType.TypeName)
    '        Throw New Exception(getClassNameAndFunction(GetCurrentMethod.Name) & "Error getting day data provider")
    '    End If

    '    If (FullAware = True) = True Then
    '        claObj = VerifReports_ClassObject(unvObj, targetMType.TypeName, "Counters", "", mts) 'Get measurement objects
    '        If claObj Is Nothing Then
    '            Trace.WriteLine("Couldn't find class object for " & rankMeasType.TypeName & ", exiting report generation for " & targetMType.TypeName)
    '            Throw New Exception(getClassNameAndFunction(GetCurrentMethod.Name) & "Error getting class object")
    '        End If
    '        claObj2 = VerifReports_ClassObject(claObj, targetMType.TypeName, "", mts) 'Get measurement objects
    '    Else
    '        claObj = VerifReports_ClassObject(unvObj, targetMType.TypeName, "Counters", "_BH", mts) 'Get measurement objects
    '        If claObj Is Nothing Then
    '            Trace.WriteLine("Couldn't find class object for " & rankMeasType.TypeName & ", exiting report generation for " & targetMType.TypeName)
    '            Throw New Exception(getClassNameAndFunction(GetCurrentMethod.Name) & "Error getting class object")
    '        End If
    '        claObj2 = VerifReports_ClassObject(claObj, targetMType.TypeName, "_BH", mts) 'Get measurement objects
    '    End If

    '    VerifReports_AddObjects(daybhDataProvider, targetMType.TypeName, "DAYBH_DAY") 'Add objects
    '    VerifReports_AddClassKeyObjects(daybhDataProvider, claObj2, excludedKeyObjectsForRankBH, mt, hiddenObjects, updatedObjects) 'Add measurement keys

    '    If (FullAware = True) Then
    '        VerifReports_AddClassObjects(daybhDataProvider, claObj, targetMType.TypeName, targetMType.Counters, "", False, fromCounter, CountersPerVerificationReport) 'Add measurement counters
    '    Else
    '        VerifReports_AddClassBHObjects(daybhDataProvider, claObj, targetMType.TypeName, targetMType.Counters) 'Add measurement counters
    '    End If

    '    'Add topology conditions
    '    VerifReports_AddConditions(Doc, daybhDataProvider, targetMType.TypeName, "TOPOLOGY")

    '    If FullAware = True Then
    '        VerifReports_AddKeyConditions(Doc, daybhDataProvider, mt.TypeName, "KEYTOPOLOGY", "") 'Add topology conditions
    '    Else
    '        VerifReports_AddKeyConditions(Doc, daybhDataProvider, mt.TypeName, "KEYTOPOLOGY", "_BH") 'Add topology conditions
    '    End If
    '    VerifReports_AddConditions(Doc, daybhDataProvider, mt.TypeName, "DAYBH") 'Add time conditions
    '    VerifReports_AddConditions(Doc, daybhDataProvider, mt.TypeName, "DAYBH_DAY") 'Add time conditions

    '    'Unload data provider for day busy hour:
    '    unloadDataProvider("DayBH", daybhDataProvider, "Rank busy hour, DAYBH table", rankMeasType.TypeName)
    '    Return daybhDataProvider
    'End Function

    ''
    ' Gets the starting counter number for this report.
    '
    '@param reportCount The number of the report being generated.
    '@param CountersPerVerificationReport The number of counters per report.
    '@returns The starting counter index for this report.
    Public Function getStartingCounter(ByVal reportCount As Integer, ByVal CountersPerVerificationReport As Integer) As Integer
        Dim startingCounter As Integer
        startingCounter = 0

        If reportCount = 1 Then
            startingCounter = 1
        Else
            startingCounter = (reportCount - 1) * CountersPerVerificationReport + 1
        End If

        Return startingCounter
    End Function

    ''
    'Check if measurementType is an Element busy hour
    '@param measurementType
    '@returns True if measurementType.ElementBusyHours is True
    Public Function isThisAnElementBusyHour(ByVal measurementType As MeasurementTypesTPIde.MeasurementType) As Boolean
        Dim returnValue As Boolean
        returnValue = False

        If (measurementType Is Nothing) Then
            returnValue = False
        ElseIf (measurementType.ElementBusyHours = True) Then
            returnValue = True
        End If
        Return returnValue
    End Function

    ''
    '@param unvObj
    '@param mtype
    '
    '@returns
    Public Function getBusyHourRankingReportClass(ByRef mtype As MeasurementTypesTPIde.MeasurementType, _
                                             ByVal parentClassName As String, ByVal extension As String) As Designer.Class
        Dim parentClass As Designer.Class
        Dim subClass As Designer.Class

        Try
            parentClass = universeForReports.Classes.Item(parentClassName)
            subClass = parentClass.Classes.Item(mtype.TypeName & extension)
        Catch ex As Exception
            Trace.WriteLine("Class 'Busy Hour/" & mtype.TypeName & "' not found in universe.")
            Trace.WriteLine("Class Exception: " & ex.ToString)
        End Try

        If subClass Is Nothing Then
            Trace.WriteLine("Class 'Busy Hour/" & mtype.TypeName & "' not found in universe.")
        End If
        Return subClass
    End Function

    ''
    'Gets the file name for a rank busy hour report.
    '@param     reportCount     The current report number.
    '@param     totalReports    The total number of reports. Will be more than 1 if there are a high number of counters 
    '                           and they need to be split into separate reports.
    '@param     mainMeasType    The rank busy hour measurement type.
    '@param     targetMeasType  The measurement type included in the busy hour.
    '@returns   reportName      The filename string.
    Public Function getRankBHReportName(ByVal reportCount As Integer, ByVal totalReports As Integer, ByVal mainMeasType As String, _
                                 ByVal targetMeasType As String, ByVal universeExtension As String) As String
        Dim reportName As String
        reportName = ""
        If (mainMeasType Is Nothing Or targetMeasType Is Nothing Or universeExtension Is Nothing) Then
            Console.WriteLine("Error getting rank busy hour report name")
        Else
            reportName = "Verification_" & mainMeasType & "_RANKBH_" & targetMeasType

            If (universeExtension <> "") Then
                reportName &= "_" & universeExtension
            End If

            If (reportCount > 0 And reportCount <= totalReports And totalReports > 1) Then
                reportName &= "_" & reportCount
            End If
        End If

        Return reportName
    End Function

    ''
    'Formats and saves the report. Applies the template.
    '@param boDocument
    '@param rankDataProvider
    '@param rawDataProvider
    '@param dayDataProvider
    '@param boVersion
    '@param mainMeasType
    '@param targetMeasType
    '@param reportName
    '@param OutputDir
    'Public Sub formatAndSaveReport(ByVal boDocument As busobj.Document, ByVal rankDataProvider As busobj.DataProvider, _
    '                      ByVal rawDataProvider As busobj.DataProvider, ByVal dayDataProvider As busobj.DataProvider, _
    '                      ByRef boVersion As String, ByVal mainMeasType As String, ByVal targetMeasType As MeasurementTypesTPIde.MeasurementType, ByVal reportName As String, _
    '                      ByVal OutputDir As String, ByVal elemBusyHour As Boolean)
    '    Dim boReport As busobj.Report
    '    Dim reportProxy As IReportProxy = New ReportProxy()
    '    Try
    '        Trace.WriteLine("Formatting and saving RANKBH report: " & reportName)
    '        boReport = makeRankBHReport(boDocument, rankDataProvider, boVersion, mainMeasType, targetMeasType.TypeName)

    '        Trace.WriteLine("Building report tables for " & reportName)
    '        buildRankBHReportTables(boDocument, boReport, rankDataProvider, rawDataProvider, dayDataProvider, elemBusyHour, reportProxy)

    '        VerifReports_FormatColumns(boDocument, targetMeasType.TypeName, "RANKBH", targetMeasType.Counters)
    '        VerifReports_FormatColumns(boDocument, targetMeasType.TypeName, "RAW", targetMeasType.Counters)
    '        If Not (elemBusyHour) Then
    '            VerifReports_FormatColumns(boDocument, targetMeasType.TypeName, "DayBH", targetMeasType.Counters)
    '        End If
    '        SaveReport(boDocument, OutputDir, reportName)
    '    Catch ex As Exception
    '        Trace.WriteLine("Report Create Error for '" & reportName & "'.")
    '        Trace.WriteLine("Report Create Error for '" & reportName & "'. Check report object and conditions for levels DAYBH_RAW, TOPOLOGY and KEYTOPOLOGY")
    '        Trace.WriteLine("Report Create Exception" & ex.ToString)
    '        SaveReport(boDocument, OutputDir, reportName)
    '    End Try
    'End Sub

    ''
    ' Create the rank busy hour report and apply settings.
    ' 
    ' @param boDocument
    ' @param dataProvider
    ' @param boVersion
    ' @param mainMeasType
    ' @param targetMeasType
    ' 
    ' @returns A new busobj.Report
    'Public Function makeRankBHReport(ByVal boDocument As busobj.Document, ByVal dataProvider As busobj.DataProvider, ByRef boVersion As String, _
    '                              ByVal mainMeasType As String, ByVal targetMeasType As String) As busobj.Report
    '    Trace.WriteLine("makeRankBHReport(): making report and applying template")
    '    Dim reportTemplateFilename As String
    '    reportTemplateFilename = "Verification_RANKBH_Template_XI.ret"
    '    Dim boReport As busobj.Report
    '    boReport = boDocument.Reports.CreateQuickReport(dataProvider.Name)

    '    ' Set the report name
    '    Dim reportName = "Busy Hour Ranking report for " & mainMeasType & " (" & targetMeasType & " data)"
    '    If (boVersion = "XI") Then
    '        applyReportSettings(boDocument, boReport, reportTemplateFilename, reportName)
    '    Else
    '        applyReportSettings(boDocument, boReport, reportTemplateFilename, reportName)
    '    End If
    '    Return boReport
    'End Function

    ''
    'Builds the tables within a rank busy report.
    '@param document            BO document
    '@param report              BO report
    '@param rankDataProvider    Data provider for the rankbh table.
    '@param rawDataProvider     Data provider for the raw table 
    '@param dayDataProvider     Data provider for the day table.
    '@param elemBusyHour        True if the busy hour is an element busy hour.
    '@remark Will delete the day busy hour table if the busy hour is an element busy hour.
    'In this case, a row is written to the elembh_rankbh table but data is not copied from the raw table to the
    'daybh table.
    'Public Sub buildRankBHReportTables(ByRef document As busobj.Document, ByRef report As busobj.Report, ByRef rankDataProvider As busobj.DataProvider, _
    '                                ByRef rawDataProvider As busobj.DataProvider, ByRef dayDataProvider As busobj.DataProvider, _
    '                                ByVal elemBusyHour As Boolean, ByVal reportProxy As IReportProxy)

    '    Dim boRepStrucItem As busobj.ReportStructureItem
    '    Dim boRepStrucItems As busobj.ReportStructureItems

    '    Dim boBlockStruc As busobj.BlockStructure
    '    Dim boPiv As busobj.Pivot
    '    Dim boSecStruc As busobj.SectionStructure

    '    Dim reportStructureItem As Object

    '    boSecStruc = report.GeneralSectionStructure
    '    boRepStrucItems = boSecStruc.Body

    '    ' Loop through all of the variables on the report
    '    For reportStructureItem = 1 To boRepStrucItems.Count
    '        boRepStrucItem = boRepStrucItems.Item(reportStructureItem)
    '        'If the report structure object is a table
    '        If boRepStrucItem.Type = busobj.BoReportItemType.boTable Then
    '            boBlockStruc = convertReportStructureItem(boRepStrucItem)

    '            If boBlockStruc.Name = "Rank Table" Then
    '                reportProxy.buildRankBHTable(document, boBlockStruc, rankDataProvider, "Rank Table", "(RANKBH)", "dummy_rank")
    '            ElseIf boBlockStruc.Name = "Raw Table" Then
    '                reportProxy.buildRankBHTable(document, boBlockStruc, rawDataProvider, "Raw Table", "(RAW)", "dummy_raw")
    '                ' Add extra BH Criteria variable to raw table:
    '                reportProxy.addVariableToTable(rawDataProvider, boBlockStruc, document)
    '            ElseIf boBlockStruc.Name = "Day Table" Then
    '                If (elemBusyHour) Then
    '                    ' Delete this table, not needed in element busy hour report:
    '                    boBlockStruc.Delete()
    '                Else
    '                    reportProxy.buildRankBHTable(document, boBlockStruc, dayDataProvider, "Day Table", "(DayBH)", "dummy_day")
    '                End If
    '            End If
    '        End If
    '    Next reportStructureItem

    '    boRepStrucItem = Nothing
    '    boRepStrucItems = Nothing
    '    boBlockStruc = Nothing
    '    boPiv = Nothing
    'End Sub

    ''
    'Converts a ReportStructureItem to a BlockStructure
    '@param boRepStrucItem      The report structure
    '@returns BlockStructure    Report structure cast to a block structure
    'Protected Overridable Function convertReportStructureItem(ByVal boRepStrucItem As busobj.ReportStructureItem) As busobj.BlockStructure
    '    Return CType(boRepStrucItem, busobj.BlockStructure)
    'End Function

    ''
    ' Adds report objects to the RANKBH table in a RANKBH report.
    ' 
    '@param data_provider The data provider for the table in the report.
    '@param claObj The class in the universe where the RANKBH objects are (e.g. Busy Hour/DC_E_BSS_BSCBH, Busy Hour/DC_E_CPP_AAL2APBH etc)
    'Public Sub addRankBHTableObjects(ByRef data_provider As busobj.DataProvider, ByRef rankbhUniverseClass As Designer.Class, _
    '                                ByRef targetMType As MeasurementTypesTPIde.MeasurementType)
    '    ' Dim rankbhUniverseObject As busobj.Object
    '    Dim rankbhUniverseObject As Designer.Object
    '    ' Go through each of the objects in the class:
    '    For Each rankbhUniverseObject In rankbhUniverseClass.Objects
    '        If (rankbhUniverseObject.Name.IndexOf("BHTYPE") < 0) Then
    '            Try
    '                addResultObject(data_provider, rankbhUniverseClass.Name, rankbhUniverseObject.Name)
    '            Catch ex As Exception
    '                Trace.WriteLine("Error adding rankbh result object: " & ex.ToString())
    '            End Try
    '        End If
    '    Next
    'End Sub

    ''
    ' Adds report objects to the RANKBH table in a RANKBH report.
    ' 
    '@param data_provider The data provider for the table in the report.
    '@param claObj The class in the universe where the RANKBH objects are (e.g. Busy Hour/DC_E_BSS_BSCBH, Busy Hour/DC_E_CPP_AAL2APBH etc)
    'Public Sub addBHTypeObjectForDayBH(ByRef data_provider As busobj.DataProvider, ByRef rankbhUniverseClass As busobj.Class, _
    '                                ByRef targetMType As MeasurementTypesTPIde.MeasurementType)
    '    Dim rankbhUniverseObject As busobj.Object
    '    Dim objectName As String
    '    ' Go through each of the objects in the class:
    '    For Each rankbhUniverseObject In rankbhUniverseClass.Objects
    '        objectName = rankbhUniverseObject.Name
    '        If (objectName.IndexOf("BHTYPE (" & targetMType.TypeName & "_DAYBH)") >= 0) Then
    '            Try
    '                addResultObject(data_provider, rankbhUniverseClass.Name, rankbhUniverseObject.Name)
    '            Catch ex As Exception
    '                Trace.WriteLine("Error adding rankbh result object: " & ex.ToString())
    '            End Try
    '        End If
    '    Next
    'End Sub

    ''
    ' Displays a message box to check if the user wants to create a report.
    '
    '@param check True if 
    '@param targetType
    '@param theMType
    '@returns The choice as a String: "Yes", "No" or "Cancel"
    'Public Function createReportYesNoCancel(ByVal check As Boolean, ByVal targetType As String, ByVal theMType As MeasurementTypesTPIde.MeasurementType) As String
    '    Dim createReport As String
    '    createReport = "Yes"

    '    If check = False Then
    '        createReport = "No"
    '    End If
    '    If check = True Then
    '        Dim makeRep As MsgBoxResult
    '        makeRep = tpUtilities.displayMessageBox("Do you want to create RANKBH Busy Hour Verification report for " & theMType.TypeName & _
    '                         ", based on " & targetType & " data? Press Cancel for skip rest reports of this type.", MsgBoxStyle.YesNoCancel, _
    '                         "")
    '        If makeRep = MsgBoxResult.Yes Then
    '            createReport = "Yes"
    '        ElseIf makeRep = MsgBoxResult.Cancel Then
    '            createReport = "Cancel"
    '        ElseIf makeRep = MsgBoxResult.No Then
    '            createReport = "No"
    '        End If
    '    End If
    '    Return createReport
    'End Function

    ''
    ' Gets the number of reports.
    '
    '@param numberOfCounters The number of counters.
    '@param CountersPerVerificationReport The number of counters per verification report. 
    '@returns The number of reports to create.
    'Public Function getNumberOfReports(ByVal numberOfCounters As Integer, ByVal CountersPerVerificationReport As Integer) As Integer
    '    Dim numberOfReports = 1

    '    If (CountersPerVerificationReport <= 0) Then
    '        numberOfReports = 0
    '    ElseIf (numberOfCounters > CountersPerVerificationReport) Then

    '        'calculate number of required reports
    '        numberOfReports = Math.Ceiling(numberOfCounters / CountersPerVerificationReport)
    '    Else
    '        numberOfReports = 1
    '    End If
    '    Return numberOfReports
    'End Function

    'Private Function VerifReports_AddObjects(ByRef data_provider As busobj.DataProvider, ByRef Measurement As String, ByRef Level As String) As Object

    '    Dim Measurements() As String
    '    Dim meas_count As Integer
    '    Dim count As Integer

    '    'Make Total Verification reports
    '    For count = 1 To repobjs.Count
    '        repobj = repobjs.Item(count)
    '        Measurements = Split(repobj.MeasurementTypeID, ",")
    '        For meas_count = 0 To UBound(Measurements)
    '            If (Measurements(meas_count) = Measurement Or LCase(Measurements(meas_count)) = "all") And repobj.Level = Level Then
    '                addResultObject(data_provider, repobj.ObjectClass, repobj.Name)
    '            End If
    '        Next meas_count
    '    Next count

    'End Function
    'Private Function VerifReports_AddConditions(ByRef Doc As busobj.Document, ByRef data_provider As busobj.DataProvider, ByRef Measurement As String, ByRef Level As String) As Object

    '    Dim Measurements() As String
    '    Dim meas_count As Integer
    '    Dim count As Integer
    '    Dim Var As busobj.Variable

    '    For count = 1 To repconds.Count
    '        repcond = repconds.Item(count)
    '        Measurements = Split(repcond.MeasurementTypeID, ",")
    '        For meas_count = 0 To UBound(Measurements)
    '            If LCase(Measurements(meas_count)) = "all" And repcond.Level = Level And repcond.ObjectCondition <> "yes" Then
    '                addConditionObject(data_provider, Doc, repcond.CondClass, repcond.Name)
    '            End If
    '            If Measurements(meas_count) = Measurement And repcond.Level = Level And repcond.ObjectCondition <> "yes" Then
    '                addConditionObject(data_provider, Doc, repcond.CondClass, repcond.Name)
    '            End If
    '            If (Measurements(meas_count) = Measurement Or LCase(Measurements(meas_count)) = "all") And repcond.Level = Level And repcond.ObjectCondition = "yes" Then
    '                'Var = Doc.Variables.Add(repcond.Prompt1 & ":")
    '                Try
    '                    'data_provider.Queries.Item(1).Conditions.Add(repcond.CondClass, repcond.Name, "Equal to", (repcond.Prompt1 & ":"), "Prompt")
    '                    data_provider.Queries.Item(1).Conditions.Add(repcond.CondClass, repcond.Name)
    '                Catch ex As Exception
    '                    Trace.WriteLine("Report Condition '" & repcond.CondClass & "/" & repcond.Name & "' adding in data provider '" & data_provider.Name & "' failed.")
    '                    Trace.WriteLine("Report Condition Exception: " & ex.ToString)
    '                    Exit Function
    '                End Try
    '            End If
    '        Next meas_count
    '    Next count

    'End Function
    'Private Function VerifReports_AddClassObjects(ByRef data_provider As busobj.DataProvider, ByRef class_obj As Designer.Class, _
    '                                              ByRef Measurement As String, ByRef mt_cnts As CountersTPIde, ByRef Annex As String, _
    '                                              ByRef CountRep As Boolean, ByRef fromCounter As Integer, ByRef counterAmount As Integer) As Object

    '    Dim ClassTree() As String
    '    Dim TreeCount As Integer
    '    Dim objectName As String
    '    Dim count As Integer

    '    Dim startCount As Integer
    '    Dim doingCount As Integer

    '    If counterAmount <> 0 Then
    '        startCount = fromCounter
    '    Else
    '        startCount = 1
    '    End If
    '    doingCount = 0
    '    For cnt_count = startCount To mt_cnts.Count
    '        If counterAmount <> 0 Then
    '            doingCount += 1
    '            If doingCount > counterAmount Then
    '                Exit For
    '            End If
    '        End If

    '        cnt = mt_cnts.Item(cnt_count)
    '        If cnt.TypeName = Measurement And cnt.UnivObject <> "" Then
    '            If cnt.oneAggrFormula = True And cnt.oneAggrValue <> "" Then
    '                If cnt.oneAggrValue <> "SUM" Then
    '                    objectName = cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")"
    '                Else
    '                    objectName = cnt.UnivObject
    '                End If
    '                If cnt.UnivClass <> "" Then
    '                    ClassTree = Split(cnt.UnivClass, "//")
    '                    TreeCount = UBound(ClassTree)
    '                    addResultObject(data_provider, ClassTree(TreeCount) & Annex, objectName)
    '                Else
    '                    addResultObject(data_provider, cnt.TypeName & Annex, objectName)
    '                End If
    '            ElseIf cnt.oneAggrFormula = False Then
    '                If cnt.UnivClass <> "" Then
    '                    ClassTree = Split(cnt.UnivClass, "//")
    '                    TreeCount = UBound(ClassTree)
    '                    For count = 0 To UBound(cnt.Aggregations)
    '                        addResultObject(data_provider, ClassTree(TreeCount) & Annex, cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")")
    '                    Next count
    '                Else
    '                    For count = 0 To UBound(cnt.Aggregations)
    '                        addResultObject(data_provider, cnt.TypeName & Annex, cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")")
    '                    Next count
    '                End If
    '            Else
    '                Trace.WriteLine("Report Result Object '" & cnt.UnivObject & "' adding for Fact Table '" & cnt.MeasurementTypeID & "' in data provider '" & data_provider.Name & "' failed.")
    '            End If
    '        End If

    '    Next cnt_count
    '    If CountRep = False Then
    '        addResultObject(data_provider, Measurement & Annex, "data_coverage")
    '    End If
    '    addResultObject(data_provider, Measurement & Annex, "period_duration")


    'End Function
    'Private Function VerifReports_AddClassBHObjects(ByRef data_provider As busobj.DataProvider, ByRef class_obj As busobj.Class, ByRef Measurement As String, ByRef mt_cnts As CountersTPIde) As Object
    '    ' class_obj is not used here? TODO remove this argument
    '    Dim ClassTree() As String
    '    Dim TreeCount As Integer
    '    Dim objectName As String
    '    Dim count As Integer

    '    For cnt_count = 1 To mt_cnts.Count
    '        cnt = mt_cnts.Item(cnt_count)

    '        If cnt.TypeName = Measurement And cnt.UnivObject <> "" Then
    '            If cnt.oneAggrFormula = True And cnt.oneAggrValue <> "" Then
    '                If cnt.oneAggrValue <> "SUM" Then
    '                    objectName = cnt.UnivObject & " (" & LCase(cnt.oneAggrValue) & ")"
    '                Else
    '                    objectName = cnt.UnivObject
    '                End If
    '                If cnt.UnivClass <> "" Then
    '                    ClassTree = Split(cnt.UnivClass, "//")
    '                    TreeCount = UBound(ClassTree)
    '                    addResultObject(data_provider, ClassTree(TreeCount) & "_BH", objectName)
    '                Else
    '                    addResultObject(data_provider, cnt.TypeName & "_BH", objectName)
    '                End If
    '            ElseIf cnt.oneAggrFormula = False And cnt.oneAggrValue <> "" Then
    '                If cnt.UnivClass <> "" Then
    '                    ClassTree = Split(cnt.UnivClass, "//")
    '                    TreeCount = UBound(ClassTree)
    '                    For count = 0 To UBound(cnt.Aggregations)
    '                        addResultObject(data_provider, ClassTree(TreeCount) & "_BH", cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")")
    '                    Next count
    '                Else
    '                    For count = 0 To UBound(cnt.Aggregations)
    '                        addResultObject(data_provider, cnt.TypeName & "_BH", cnt.UnivObject & " (" & LCase(cnt.Aggregations(count)) & ")")
    '                    Next count
    '                End If

    '            Else
    '                Trace.WriteLine("Report Result Object '" & cnt.UnivObject & "' adding for Fact Table '" & cnt.TypeName & "' in data provider '" & data_provider.Name & "' failed.")
    '            End If
    '        End If
    '    Next cnt_count
    '    addResultObject(data_provider, Measurement & "_BH", "period_duration")

    'End Function

    ''
    'Adds the keys from the _Keys class to a verification report.
    '@param data_provider       The data provider in the verification report.
    '@param class_obj           The universe class 
    '@param keysToExclude       A list of keys to exclude
    '@param measType            The measurement type. Used to get the keys defined in the measurement type itself.
    '@param hiddenObjects       List of hidden objects in the universe as a String
    '@param universeObjects     List of universe objects. These are the objects defined in the Universe, Objects tab in the
    '                           IDE.
    'Public Sub VerifReports_AddClassKeyObjects(ByRef data_provider As busobj.DataProvider, ByRef class_obj As Designer.Class, _
    '                                                 ByVal keysToExclude As String(), ByVal measType As MeasurementTypesTPIde.MeasurementType, _
    '                                                     ByVal hiddenObjects As ArrayList, ByVal universeObjects As String)
    '    Dim Inx As Short
    '    'Make Total Verification reports
    '    For Inx = 1 To class_obj.Objects.Count
    '        ' Get the object name from the class
    '        Dim objectName = class_obj.Objects.Item(Inx).Name

    '        Dim addKey As Boolean = True

    '        If (isExcludedKey(objectName, keysToExclude)) Then
    '            Trace.WriteLine("VerifReports_AddClassKeyObjects(), Did not add key to report (excluded key): " & class_obj.Name & "/" & objectName)
    '            addKey = False
    '        Else
    '            Dim objectIdentifier As String = class_obj.Name & "/" & objectName & ";"
    '            Dim isHiddenObject As Boolean = (hiddenObjects.IndexOf(objectIdentifier) >= 0)

    '            If (isHiddenObject) Then
    '                ' If hidden, don't add except if defined in the measurement type keys, or in the objects:
    '                addKey = False

    '                ' Check if key is defined in measurement type keys:
    '                cnt_keys = measType.CounterKeys
    '                For cnt_key_count = 1 To cnt_keys.Count
    '                    cnt_key = cnt_keys.Item(cnt_key_count)

    '                    If (cnt_key.UnivObject = objectName) Then
    '                        addKey = True
    '                        Exit For
    '                    End If
    '                Next

    '                ' Check if key is defined in the Universe, Objects tab in the IDE:
    '                If (addKey = False) Then
    '                    Dim boObject As String
    '                    If (universeObjects.IndexOf(objectIdentifier) >= 0) Then
    '                        addKey = True
    '                    End If
    '                End If
    '            End If

    '            If (addKey = True) Then
    '                addResultObject(data_provider, class_obj.Name, objectName)
    '            Else
    '                Trace.WriteLine("VerifReports_AddClassKeyObjects(), Did not add key to report: " & class_obj.Name & "/" & objectName)
    '                Trace.WriteLine("Key was hidden but not defined in the tech pack.")
    '            End If
    '        End If
    '    Next

    'End Sub

    '
    '@param classKey The class key to check
    '@param classKey A list of class key to check
    '@returns needed A boolean value to check if the class key is needed in the report
    Protected Function isExcludedKey(ByVal classKey As String, ByVal excludedKeys As String()) As Boolean
        Dim excluded As Boolean
        ' Needed by default
        excluded = False
        Dim excludedKey As New String("")
        For Each excludedKey In excludedKeys
            If (classKey = excludedKey) Then
                ' If the key should be excluded, set this to False
                excluded = True
                Exit For
            End If
        Next
        Return excluded
    End Function

    'Sub addConditionObject(ByRef data_provider As busobj.DataProvider, ByRef Doc As busobj.Document, ByRef ClassName As String, ByRef ConditionName As String)
    '    Try
    '        data_provider.Queries.Item(1).Conditions.Add(ClassName, ConditionName)
    '        Trace.WriteLine("Added this condition: " & ClassName & ", " & ConditionName & " to " & data_provider.Name)

    '        If Not (repcond Is Nothing) Then
    '        If repcond.Prompt1 <> "" Then
    '            Doc.Variables.Item(repcond.Prompt1 & ":").Value = repcond.Value1
    '        End If
    '        If repcond.Prompt2 <> "" Then
    '            Doc.Variables.Item(repcond.Prompt2 & ":").Value = repcond.Value2
    '        End If
    '        If repcond.Prompt3 <> "" Then
    '            Doc.Variables.Item(repcond.Prompt2 & ":").Value = repcond.Value3
    '        End If
    '        End If
    '    Catch ex As Exception
    '        Trace.WriteLine("Report Condition '" & ClassName & "/" & ConditionName & "' adding in data provider '" & data_provider.Name & "' failed.")
    '        Trace.WriteLine("Report Condition Exception: " & ex.ToString)
    '        Exit Sub
    '    End Try
    'End Sub
    'Sub addResultObject(ByRef data_provider As busobj.DataProvider, ByVal ClassName As String, ByVal ObjectName As String)
    '    Try
    '            data_provider.Queries.Item(1).Results.Add(ClassName, ObjectName)
    '        Trace.WriteLine("Added object: " & ClassName & "\" & ObjectName & " to " & data_provider.Name)
    '    Catch ex As Exception
    '        Trace.WriteLine("Report Result '" & ClassName & "/" & ObjectName & "' adding in data provider '" & data_provider.Name & "' failed.")
    '        Trace.WriteLine("Report Result Exception: " & ex.ToString)
    '        Exit Sub
    '    End Try
    'End Sub

    ' Finds hidden counters in the universe.
    '@returns ArrayList with the hidden counters.
    Public Function getHiddenObjects(ByVal theClasses As Designer.Classes, ByRef hiddenObjects As ArrayList) As ArrayList
        Try
            Dim theClass As Designer.Class
            Dim theObject As Designer.Object

            ' Go through the classes in the universe, find hidden objects:
            For Each theClass In theClasses
                For Each theObject In theClass.Objects

                    If (theObject.Show = False) Then
                        Dim objectIdentifier As String
                        objectIdentifier = theClass.Name & "/" & theObject.Name & ";"
                        hiddenObjects.Add(objectIdentifier)
                        Trace.WriteLine("Found hidden object: " & objectIdentifier)
                    End If
                Next
                If (theClass.Classes.Count > 0) Then
                    getHiddenObjects(theClass.Classes, hiddenObjects)
                End If
            Next theClass
        Catch ex As Exception
            Trace.WriteLine("Error getting hidden objects: " & ex.ToString())
            Return hiddenObjects
        End Try
        Return hiddenObjects
    End Function

    'Private Function VerifReports_AddCMClassObjects(ByRef data_provider As busobj.DataProvider, ByRef class_obj As busobj.Class, ByRef Measurement As String, ByRef mt_cnts As CountersTPIde) As Object

    '    Dim ClassTree() As String
    '    Dim TreeCount As Integer
    '    Dim first As Boolean
    '    Dim tempAggregations As String
    '    Dim Aggregations() As String
    '    Dim oneAggrValue As String
    '    Dim oneAggrFormula As Boolean
    '    Dim count As Integer

    '    For cnt_count = 1 To mt_cnts.Count
    '        cnt = mt_cnts.Item(cnt_count)

    '        'get different aggregation formulas
    '        first = True
    '        tempAggregations = ""
    '        oneAggrFormula = False
    '        oneAggrValue = ""
    '        For count = 0 To UBound(cnt.TimeAggrList)
    '            If InStrRev(tempAggregations, cnt.TimeAggrList(count)) = 0 Then
    '                If first = False Then
    '                    tempAggregations &= ","
    '                End If
    '                If first = True Then
    '                    first = False
    '                End If
    '                tempAggregations &= cnt.TimeAggrList(count)
    '            End If
    '        Next count
    '        For count = 0 To UBound(cnt.GroupAggrList)
    '            If InStrRev(tempAggregations, cnt.GroupAggrList(count)) = 0 Then
    '                If first = False Then
    '                    tempAggregations &= ","
    '                End If
    '                If first = True Then
    '                    first = False
    '                End If
    '                tempAggregations &= cnt.GroupAggrList(count)
    '            End If
    '        Next count
    '        Aggregations = Split(tempAggregations, ",")
    '        If UBound(Aggregations) = 0 Then
    '            oneAggrFormula = True
    '            oneAggrValue = Aggregations(0)
    '        End If

    '        If cnt.TypeName = Measurement And cnt.UnivObject <> "" Then
    '            If oneAggrFormula = True And oneAggrValue <> "" Then
    '                If cnt.UnivClass <> "" Then
    '                    ClassTree = Split(cnt.UnivClass, "//")
    '                    TreeCount = UBound(ClassTree)
    '                    addResultObject(data_provider, ClassTree(TreeCount), cnt.UnivObject)
    '                Else
    '                    addResultObject(data_provider, cnt.TypeName, cnt.UnivObject)
    '                End If
    '            End If
    '        End If
    '    Next cnt_count

    'End Function
    'Private Function VerifReports_AddKeyConditions(ByRef Doc As busobj.Document, ByRef data_provider As busobj.DataProvider, ByRef Measurement As String, ByRef Level As String, ByRef Annex As String) As Object

    '    Dim Measurements() As String
    '    Dim meas_count As Integer
    '    Dim count As Integer
    '    Dim Var As busobj.Variable

    '    ' Iterate through the report conditions from the database:
    '    For count = 1 To repconds.Count
    '        repcond = repconds.Item(count)
    '        Measurements = Split(repcond.MeasurementTypeID, ",")
    '        For meas_count = 0 To UBound(Measurements)
    '            If (Measurements(meas_count) = "All" Or Measurements(meas_count) = Measurement) And repcond.Level = Level Then
    '                addConditionObject(data_provider, Doc, Measurement & Annex & "_Keys", repcond.Name)
    '            End If
    '        Next meas_count
    '    Next count

    'End Function

    ''
    ' Gets the class object.
    '@param unvObj
    '@param Measurement
    '@param ClassName
    '@param postFix
    '
    '@param mts
    '@returns TestClass (busobj.Class)
    '@remarks Throws exception if the class is not found.
    'Private Function VerifReports_ClassObject(ByRef unvObj As busobj.Universe, ByRef Measurement As String, ByRef ClassName As String, ByRef postFix As String, _
    '                                           ByRef mts As MeasurementTypesTPIde) As Designer.Class
    '    ' Dim TestClass As busobj.Class
    '    Dim TestClass As Designer.Class ' Get class from universe instead

    '    For mt_count = 1 To mts.Count
    '        mt = mts.Item(mt_count)
    '        If mt.TypeName = Measurement Then
    '            Try
    '                ' TestClass = unvObj.Classes.Item(ClassName).Classes.Item(mt.MeasurementTypeClassDescription & " " & ClassName).Classes.Item(Measurement & postFix)
    '                TestClass = universeForReports.Classes.Item(ClassName).Classes.Item(mt.MeasurementTypeClassDescription & " " & ClassName).Classes.Item(Measurement & postFix)
    '            Catch ex As Exception
    '                Trace.WriteLine("Class '" & mt.MeasurementTypeClassDescription & " " & ClassName & "/" & Measurement & postFix & "' not found in universe.")
    '                Trace.WriteLine("Class Exception: " & ex.ToString)
    '            End Try
    '            Exit For
    '        End If
    '    Next mt_count

    '    If TestClass Is Nothing Then
    '        Dim message As String
    '        message = "Class '" & mt.MeasurementTypeClassDescription & " " & ClassName & "/" & Measurement & postFix & "' not found in universe."
    '        Trace.WriteLine(message)
    '        Throw New Exception(message)
    '    End If
    '    Return TestClass
    'End Function

    ''
    ' Gets the class object.
    '@param unvClass
    '@param Measurement
    '@param postFix
    '
    '@param mts
    '@returns TestClass (busobj.Class)
    '@remarks Throws exception if the class is not found.
    Private Function VerifReports_ClassObject(ByRef unvClass As Designer.Class, ByRef Measurement As String, ByRef postFix As String, _
                                          ByRef mts As MeasurementTypesTPIde) As Designer.Class
        ' Dim TestClass As busobj.Class
        Dim TestClass As Designer.Class

        For mt_count = 1 To mts.Count
            mt = mts.Item(mt_count)
            If mt.TypeName = Measurement Then
                Try
                    TestClass = unvClass.Classes.Item(Measurement & postFix & "_Keys")
                Catch ex As Exception
                    Trace.WriteLine("Class '" & unvClass.Name & "/" & Measurement & postFix & "_Keys" & "' not found in universe.")
                    Trace.WriteLine("Class Exception: " & ex.ToString)
                End Try
                Exit For
            End If
        Next mt_count

        If TestClass Is Nothing Then
            Dim message As String
            message = "Class '" & unvClass.Name & "/" & Measurement & postFix & "_Keys" & "' not found in universe."
            Trace.WriteLine(message)
            Throw New Exception(message)
        End If

        Return TestClass
    End Function

    'Private Function VerifReports_FormatColumns(ByRef Doc As busobj.Document, ByRef Measurement As String, ByRef DataProv As String, ByRef cnts As CountersTPIde) As Object

    '    Dim DocVar As busobj.DocumentVariable
    '    Dim i As Short
    '    Dim intSubStringLoc As Short
    '    Dim intSubStringLoc2 As Short
    '    Dim tempHeader As String

    '    Dim ObjRowCount As Short

    '    Dim ColNames() As String

    '    For i = 1 To Doc.DocumentVariables.Count
    '        If InStrRev(Doc.DocumentVariables(i).Formula, "=NameOf(<") > 0 AndAlso InStrRev(Doc.DocumentVariables(i).Formula, "(" & DataProv & ")>") > 0 Then
    '            intSubStringLoc = InStr(Doc.DocumentVariables(i).Formula, "=NameOf(<")
    '            intSubStringLoc2 = InStr(Doc.DocumentVariables(i).Formula, "(" & DataProv & ")>")
    '            tempHeader = Mid(Doc.DocumentVariables(i).Formula, intSubStringLoc + 9, intSubStringLoc2 - (intSubStringLoc + 9))
    '            Doc.DocumentVariables(i).Formula = tempHeader

    '            For cnt_count = 1 To cnts.Count
    '                cnt = cnts.Item(cnt_count)

    '                ColNames = Split(tempHeader, " ")

    '                If cnt.TypeName = Measurement AndAlso StrComp(ColNames(0), cnt.UnivObject) = 0 AndAlso cnt.UnivObject <> "" Then
    '                    Doc.DocumentVariables(i).Formula = tempHeader
    '                    Exit For
    '                End If
    '            Next cnt_count
    '        End If
    '    Next i
    'End Function
    'Sub VerifReports_BuildRawReportTables(ByRef document As busobj.Document, ByRef report As busobj.Report)

    '    Dim boRepStrucItem As busobj.ReportStructureItem
    '    Dim boRepStrucItems As busobj.ReportStructureItems

    '    Dim boRepFooterItem As busobj.ReportStructureItem
    '    Dim boRepFooterItems As busobj.ReportStructureItems

    '    Dim boBlockStruc As busobj.BlockStructure
    '    Dim boPiv As busobj.Pivot
    '    Dim boSecStruc As busobj.SectionStructure

    '    Dim i As Object
    '    Dim j As Short

    '    boSecStruc = report.GeneralSectionStructure
    '    boRepStrucItems = boSecStruc.Body

    '    ' Loop through all of the variables on the report
    '    For i = 1 To boRepStrucItems.Count
    '        boRepStrucItem = boRepStrucItems.Item(i)
    '        'If the report structure object is a table
    '        If boRepStrucItem.Type = busobj.BoReportItemType.boTable Then
    '            boBlockStruc = boRepStrucItem
    '            ' If Table is for raw counters (Table 1)
    '            If boBlockStruc.Name = "Table 1" Then
    '                boPiv = boBlockStruc.Pivot
    '                'remove "dummy" object from the table
    '                For j = 1 To boPiv.BodyCount
    '                    If boPiv.Body(j).Name = "dummy" Then
    '                        boPiv.Body(j).Delete()
    '                        boPiv.Apply()
    '                    End If
    '                Next j
    '            End If
    '        End If
    '    Next i

    '    boRepStrucItem = Nothing
    '    boRepStrucItems = Nothing
    '    boBlockStruc = Nothing
    '    boPiv = Nothing

    'End Sub
    'Sub VerifReports_BuildTotalReportTables(ByRef document As busobj.Document, ByRef report As busobj.Report, ByRef d_prv As busobj.DataProvider)

    '    Dim boRepStrucItem As busobj.ReportStructureItem
    '    Dim boRepStrucItems As busobj.ReportStructureItems

    '    Dim boRepFooterItem As busobj.ReportStructureItem
    '    Dim boRepFooterItems As busobj.ReportStructureItems

    '    Dim boBlockStruc As busobj.BlockStructure
    '    Dim boPiv As busobj.Pivot
    '    Dim boSecStruc As busobj.SectionStructure

    '    Dim i As Object
    '    Dim j As Short

    '    boSecStruc = report.GeneralSectionStructure
    '    boRepStrucItems = boSecStruc.Body

    '    Dim variable As busobj.DocumentVariable

    '    ' Loop through all of the variables on the report
    '    For i = 1 To boRepStrucItems.Count
    '        boRepStrucItem = boRepStrucItems.Item(i)
    '        'If the report structure object is a table
    '        If boRepStrucItem.Type = busobj.BoReportItemType.boTable Then
    '            boBlockStruc = boRepStrucItem
    '            ' If Table is for day counters (Table 1.0)
    '            If boBlockStruc.Name = "Table 1.0" Then
    '                boPiv = boBlockStruc.Pivot
    '                'Add day counters to table
    '                Trace.WriteLine("Building Total report tables: DAY")
    '                For j = 1 To d_prv.Columns.Count
    '                    Trace.WriteLine("Adding: " & d_prv.Columns(j).Name)
    '                    Try
    '                        If InStrRev(d_prv.Columns(j).Name, "(none)") = 0 Then
    '                            boPiv.Body(j) = document.DocumentVariables(d_prv.Columns(j).Name + "(DAY)")
    '                        End If
    '                    Catch ex As Exception
    '                        'Trace.WriteLine("Error in '" & d_prv.Columns(j).Name & "(DAY)' at '" & d_prv.Name & "'. Retrying.")
    '                        Try
    '                            boPiv.Body(j) = document.DocumentVariables(d_prv.Columns(j).Name)
    '                            'Trace.WriteLine("'" & d_prv.Columns(j).Name & "(DAY)' at '" & d_prv.Name & "' retry successful.")
    '                        Catch e As Exception
    '                            Trace.WriteLine("Error in '" & d_prv.Columns(j).Name & "' at '" & d_prv.Name & "'.")
    '                            Trace.WriteLine("Class Exception: " & e.ToString)
    '                        End Try
    '                    End Try
    '                Next j
    '                boPiv.Apply()
    '            End If

    '            If boBlockStruc.Name = "Table 1.1" Then
    '                boPiv = boBlockStruc.Pivot
    '                'Add day counters to table
    '                For j = 1 To d_prv.Columns.Count
    '                    Try
    '                        If InStrRev(d_prv.Columns(j).Name, "(none)") = 0 Then
    '                            boPiv.Body(j) = document.DocumentVariables(d_prv.Columns(j).Name + "(RAW)")
    '                            'Console.WriteLine(boPiv.Body(j).Formula)
    '                        End If
    '                    Catch ex As Exception
    '                        Trace.WriteLine("Error in '" & d_prv.Columns(j).Name & "(RAW)' at '" & d_prv.Name & "'.")
    '                        Trace.WriteLine("Class Exception: " & ex.ToString)
    '                    End Try
    '                Next j
    '                boPiv.Apply()
    '            End If
    '        End If
    '    Next i

    '    ' Loop through all of the variables on the report
    '    For i = 1 To boRepStrucItems.Count
    '        boRepStrucItem = boRepStrucItems.Item(i)
    '        'If the report structure object is a table
    '        If boRepStrucItem.Type = busobj.BoReportItemType.boTable Then
    '            boBlockStruc = boRepStrucItem
    '            ' If Table is for raw counters (Table 1)
    '            If boBlockStruc.Name = "Table 1.0" Then
    '                boPiv = boBlockStruc.Pivot
    '                'remove "dummy" object from the table
    '                For j = 1 To boPiv.BodyCount
    '                    If boPiv.Body(j).Name = "dummy" Then
    '                        'boPiv.Body(j).Delete()
    '                        'boPiv.Apply()
    '                    End If
    '                Next j
    '            End If
    '        End If
    '    Next i

    '    boRepStrucItem = Nothing
    '    boRepStrucItems = Nothing
    '    boBlockStruc = Nothing
    '    boPiv = Nothing

    'End Sub
    Public Function Universe_ListContexts() As Boolean
        Dim DesignerApp As Designer.Application
        Dim retry As Boolean
        Dim Univ As Designer.Universe
        DesignerApp = Nothing

        Try
            DesignerApp = New Designer.Application
            DesignerApp.Visible = False
            DesignerApp.LogonDialog()
        Catch ex As Exception
            Console.WriteLine("BO Exception: " + ex.ToString)
        End Try

        retry = True
        While retry = True
            Try
                retry = False
                Univ = DesignerApp.Universes.Open
            Catch ex As Exception
                System.Threading.Thread.Sleep(5000)
                retry = True
            End Try
        End While

        Dim Jn As Designer.Join
        Dim Cntxt As Designer.Context
        Dim Count As Integer
        Dim ItemCount As Integer

        Try
            For Count = 1 To Univ.Contexts.Count
                Cntxt = Univ.Contexts.Item(Count)
                For ItemCount = 1 To Cntxt.Joins.Count
                    Jn = Cntxt.Joins.Item(ItemCount)
                    Console.WriteLine(Jn.Expression)
                Next
            Next
        Catch ex As Exception
            Console.WriteLine("Exception on listing joins:" & ex.ToString)
            Return False
        End Try

        DesignerApp.Visible = True
        DesignerApp.Interactive = True
        DesignerApp.Quit()

        Return True
    End Function
    'Sub VerifReports_BuildBHReportTables(ByRef document As busobj.Document, ByRef report As busobj.Report, ByRef d_prv As busobj.DataProvider)

    '    Dim boRepStrucItem As busobj.ReportStructureItem
    '    Dim boRepStrucItems As busobj.ReportStructureItems
    '    Dim boBlockStruc As busobj.BlockStructure
    '    Dim boPiv As busobj.Pivot
    '    Dim boSecStruc As busobj.SectionStructure
    '    Dim DocVar As busobj.DocumentVariable
    '    Dim Found As Boolean

    '    Dim i As Object
    '    Dim j As Short

    '    boSecStruc = report.GeneralSectionStructure
    '    boRepStrucItems = boSecStruc.Body

    '    ' Loop through all of the variables on the report
    '    For i = 1 To boRepStrucItems.Count
    '        boRepStrucItem = boRepStrucItems.Item(i)
    '        'If the report structure object is a table
    '        If boRepStrucItem.Type = busobj.BoReportItemType.boTable Then
    '            boBlockStruc = boRepStrucItem
    '            ' If Table is for raw counters (Table 1)
    '            If boBlockStruc.Name = "Table 1" Then
    '                boPiv = boBlockStruc.Pivot
    '                'remove "dummy" object from the table
    '                For j = 1 To boPiv.BodyCount
    '                    If boPiv.Body(j).Name = "dummy" Then
    '                        boPiv.Body(j).Delete()
    '                        boPiv.Apply()
    '                        Exit For
    '                    End If
    '                Next j

    '            End If
    '            ' If Table is for day counters (Table 1.0)
    '            If boBlockStruc.Name = "Table 1.0" Then
    '                boPiv = boBlockStruc.Pivot
    '                'Add day counters to table

    '                Dim columnCount = d_prv.Columns.Count
    '                If (columnCount <= 0) Then
    '                    Trace.WriteLine("VerifReports_BuildBHReportTables(): Error. No busy hour data found when building report." _
    '                                    & Environment.NewLine & "Please check the levels defined in the verification objects and verification conditions.")
    '                Else
    '                    ' Go through columns:
    '                    For j = 1 To d_prv.Columns.Count
    '                        Try
    '                            If InStrRev(d_prv.Columns(j).Name, "(none)") = 0 Then
    '                                If d_prv.Columns(j).Name = "Busy Hour" Then
    '                                    boPiv.Body(j) = document.DocumentVariables("Busy Hour")
    '                                ElseIf d_prv.Columns(j).Name = "Element Name" Then
    '                                    boPiv.Body(j) = document.DocumentVariables("Element Name")
    '                                Else
    '                                    boPiv.Body(j) = document.DocumentVariables(d_prv.Columns(j).Name & "(DAY)")
    '                                End If
    '                            End If
    '                        Catch ex As Exception
    '                            Try
    '                                boPiv.Body(j) = document.DocumentVariables(d_prv.Columns(j).Name)
    '                            Catch e As Exception
    '                                Trace.WriteLine("Error in '" & d_prv.Columns(j).Name & "(DAY)' at '" & d_prv.Name & "'.")
    '                                Trace.WriteLine("Class Exception: " & ex.ToString)
    '                                Trace.WriteLine("Retry Error in '" & d_prv.Columns(j).Name & "(DAY)' at '" & d_prv.Name & "'.")
    '                                Trace.WriteLine("Class Exception: " & e.ToString)
    '                            End Try
    '                        End Try
    '                    Next j
    '                    boPiv.Apply()
    '                End If
    '            End If
    '        End If
    '    Next i

    '    boRepStrucItem = Nothing
    '    boRepStrucItems = Nothing
    '    boBlockStruc = Nothing
    '    boPiv = Nothing

    'End Sub

    ''
    ' Gets class name and function for logging.
    '@param functionName
    '@returns   Class name and function
    Private Function getClassNameAndFunction(ByVal functionName As String) As String
        Dim classNameAndFunction As String
        classNameAndFunction = className & "," & functionName & ": "
        Return classNameAndFunction
    End Function

End Class
