package com.example.util

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import com.example.data.UserRole
import com.example.ui.theme.*
import com.example.viewmodel.AuthFlowStep
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Tenant Action types for rigid role-based access control.
 */
enum class TenantAction {
    MANAGE_STAFF,
    APPROVE_FEES,
    COLLECT_FEES,
    VIEW_MASTER_LEDGER,
    EDIT_SCHOOL_PROFILE,
    VIEW_TEACHER_SALARY_ALL,
    VIEW_OWN_TEACHER_SALARY,
    VIEW_DRIVER_SALARY_ALL,
    VIEW_OWN_DRIVER_SALARY,
    REGISTER_STUDENT,
    DELETE_STUDENT,
    DELETE_STAFF,
    BROADCAST_ANNOUNCEMENT,
    ACCESS_SUPER_ADMIN_CONSOLE,
    SCAN_EXAM_PAPERS,
    TAKE_ATTENDANCE,
    TRACK_BUS_GPS,
    BROADCAST_BUS_GPS,
    VIEW_STUDENT_REPORT_CARD,
    ACCESS_CLASS_CHAT
}

/**
 * Data snapshot representing the current authentication state.
 */
data class AuthSessionState(
    val user: UserProfile? = null,
    val activeRole: UserRole = UserRole.PRINCIPAL,
    val schoolTenantId: String = "SCH-DEL-001",
    val linkedSchoolIds: List<String> = emptyList(),
    val isMpinAuthenticated: Boolean = true,
    val isBiometricAuthenticated: Boolean = false,
    val authStep: AuthFlowStep = AuthFlowStep.AUTHENTICATED,
    val sessionToken: String? = "AUTH-TOKEN-DEFAULT"
)

/**
 * AuthenticationStateHolder:
 * Monitors the user's role and school tenant ID, ensuring navigation is restricted
 * and components are dynamically swapped based on the logged-in user's role isolation rules.
 */
object AuthenticationStateHolder {

    private val _authState = MutableStateFlow(AuthSessionState())
    val authState: StateFlow<AuthSessionState> = _authState.asStateFlow()

    val currentRole: UserRole
        get() = _authState.value.activeRole

    val currentTenantId: String
        get() = _authState.value.schoolTenantId

    val currentUser: UserProfile?
        get() = _authState.value.user

    val isAuthenticated: Boolean
        get() = _authState.value.authStep == AuthFlowStep.AUTHENTICATED && _authState.value.isMpinAuthenticated

    /**
     * Update the active session whenever the user logs in, switches role, or selects a school.
     */
    fun updateSession(
        user: UserProfile?,
        role: UserRole,
        schoolTenantId: String,
        linkedSchoolIds: List<String> = user?.linkedSchoolIds ?: emptyList(),
        authStep: AuthFlowStep = AuthFlowStep.AUTHENTICATED,
        isMpinAuthenticated: Boolean = true,
        isBiometricAuthenticated: Boolean = false
    ) {
        _authState.update {
            it.copy(
                user = user,
                activeRole = role,
                schoolTenantId = schoolTenantId,
                linkedSchoolIds = linkedSchoolIds,
                authStep = authStep,
                isMpinAuthenticated = isMpinAuthenticated,
                isBiometricAuthenticated = isBiometricAuthenticated,
                sessionToken = "SESSION-${role.name}-${schoolTenantId}-${System.currentTimeMillis()}"
            )
        }
    }

    /**
     * Update active school tenant ID.
     */
    fun setTenantId(newTenantId: String) {
        _authState.update { it.copy(schoolTenantId = newTenantId) }
    }

    /**
     * Update active role.
     */
    fun setActiveRole(newRole: UserRole) {
        _authState.update { it.copy(activeRole = newRole) }
    }

    /**
     * Lock the current session.
     */
    fun lockSession() {
        _authState.update {
            it.copy(
                authStep = AuthFlowStep.MPIN_LOGIN,
                isMpinAuthenticated = false,
                isBiometricAuthenticated = false
            )
        }
    }

    /**
     * Logout and clear the current session.
     */
    fun logout() {
        _authState.update {
            it.copy(
                user = null,
                authStep = AuthFlowStep.MOBILE_VERIFICATION,
                isMpinAuthenticated = false,
                isBiometricAuthenticated = false,
                sessionToken = null
            )
        }
    }

    /**
     * Validates whether a specific screen is authorized for a given role.
     */
    fun isScreenAuthorized(role: UserRole, screenRoute: String): Boolean {
        return when (screenRoute) {
            "SUPER_ADMIN" -> role == UserRole.SUPER_ADMIN
            "FEES" -> role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN
            "TEACHER_SALARY" -> role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN || role == UserRole.TEACHER
            "DRIVER_SALARY" -> role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN || role == UserRole.DRIVER
            "REGISTER_STUDENT" -> role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN
            "STAFF_REPLACE" -> role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN
            "REGISTER_PRINCIPAL" -> role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN
            "REGISTER_STAFF" -> role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN
            "ATTENDANCE" -> role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN || role == UserRole.TEACHER
            "EXAM_SCANNER" -> role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN || role == UserRole.TEACHER
            "GPS" -> role == UserRole.PARENT_STUDENT || role == UserRole.DRIVER || role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN
            "CLASS_CHAT" -> role == UserRole.TEACHER || role == UserRole.PARENT_STUDENT || role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN
            "DASHBOARD", "SUVICHAR", "GALLERY", "HELP_BOT", "SECURITY" -> true
            else -> true
        }
    }

    /**
     * Resolves the safe authorized screen target. If the user is unauthorized,
     * it falls back safely to DASHBOARD.
     */
    fun resolveAuthorizedScreen(role: UserRole, targetRoute: String): String {
        return if (isScreenAuthorized(role, targetRoute)) {
            // Map legacy alias REGISTER_STAFF to TEACHER_SALARY if allowed
            if (targetRoute == "REGISTER_STAFF") "TEACHER_SALARY" else targetRoute
        } else {
            "DASHBOARD"
        }
    }

    /**
     * Verifies if a specific action is permitted under the active user role.
     */
    fun isActionPermitted(action: TenantAction, role: UserRole = currentRole): Boolean {
        return when (action) {
            TenantAction.ACCESS_SUPER_ADMIN_CONSOLE -> role == UserRole.SUPER_ADMIN
            TenantAction.MANAGE_STAFF,
            TenantAction.APPROVE_FEES,
            TenantAction.VIEW_MASTER_LEDGER,
            TenantAction.EDIT_SCHOOL_PROFILE,
            TenantAction.VIEW_TEACHER_SALARY_ALL,
            TenantAction.VIEW_DRIVER_SALARY_ALL,
            TenantAction.REGISTER_STUDENT,
            TenantAction.DELETE_STUDENT,
            TenantAction.DELETE_STAFF,
            TenantAction.BROADCAST_ANNOUNCEMENT -> role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN
            TenantAction.COLLECT_FEES,
            TenantAction.SCAN_EXAM_PAPERS,
            TenantAction.TAKE_ATTENDANCE,
            TenantAction.VIEW_OWN_TEACHER_SALARY -> role == UserRole.TEACHER || role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN
            TenantAction.VIEW_OWN_DRIVER_SALARY,
            TenantAction.BROADCAST_BUS_GPS -> role == UserRole.DRIVER || role == UserRole.SUPER_ADMIN
            TenantAction.TRACK_BUS_GPS -> role == UserRole.PARENT_STUDENT || role == UserRole.DRIVER || role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN
            TenantAction.VIEW_STUDENT_REPORT_CARD,
            TenantAction.ACCESS_CLASS_CHAT -> role == UserRole.PARENT_STUDENT || role == UserRole.TEACHER || role == UserRole.PRINCIPAL || role == UserRole.SUPER_ADMIN
        }
    }

    /**
     * Rigid multi-tenant check: Confirms if a target record belonging to targetTenantSchoolId
     * can be accessed or modified by the user.
     */
    fun isTenantMatch(
        targetTenantSchoolId: String,
        userTenantId: String = currentTenantId,
        userRole: UserRole = currentRole,
        linkedTenantIds: List<String> = _authState.value.linkedSchoolIds
    ): Boolean {
        // Super Admin can access all schools
        if (userRole == UserRole.SUPER_ADMIN) return true

        // Exact school match
        if (targetTenantSchoolId.equals(userTenantId, ignoreCase = true)) return true

        // Multi-school teacher mapping check
        if (userRole == UserRole.TEACHER && linkedTenantIds.contains(targetTenantSchoolId)) {
            return true
        }

        return false
    }
}

/**
 * CompositionLocal for AuthenticationStateHolder.
 */
val LocalAuthState = compositionLocalOf { AuthenticationStateHolder.authState.value }

/**
 * Composable that swaps its content based on the user's active role.
 */
@Composable
fun RoleIsolatedContent(
    activeRole: UserRole,
    principalContent: @Composable () -> Unit,
    teacherContent: @Composable () -> Unit,
    parentStudentContent: @Composable () -> Unit,
    driverContent: @Composable () -> Unit,
    superAdminContent: @Composable () -> Unit
) {
    when (activeRole) {
        UserRole.PRINCIPAL -> principalContent()
        UserRole.TEACHER -> teacherContent()
        UserRole.PARENT_STUDENT -> parentStudentContent()
        UserRole.DRIVER -> driverContent()
        UserRole.SUPER_ADMIN -> superAdminContent()
    }
}

/**
 * Guard Composable that enforces permission requirements for critical UI modules.
 */
@Composable
fun RequireRole(
    permittedRoles: Set<UserRole>,
    activeRole: UserRole,
    isHindi: Boolean = false,
    fallback: @Composable () -> Unit = {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = CrimsonRed.copy(alpha = 0.08f),
            border = androidx.compose.foundation.BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.3f)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Restricted Access",
                    tint = CrimsonRed,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (isHindi) "पहुँच प्रतिबंधित (Role Isolation Active)" else "Access Restricted (Role Isolation)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CrimsonRed
                    )
                    Text(
                        text = if (isHindi) "यह मॉड्यूल आपकी भूमिका (${activeRole.labelHi}) के लिए अधिकृत नहीं है।"
                        else "This module is not authorized for your current role (${activeRole.labelEn}).",
                        fontSize = 11.sp,
                        color = TextDarkSecondary
                    )
                }
            }
        }
    },
    content: @Composable () -> Unit
) {
    if (permittedRoles.contains(activeRole)) {
        content()
    } else {
        fallback()
    }
}

/**
 * Role & Tenant Visual Indicator badge.
 */
@Composable
fun TenantRoleBadge(
    role: UserRole,
    tenantSchoolId: String,
    schoolName: String,
    isHindi: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = RoyalPurple900.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalPurple800.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Tenant Isolation",
                tint = RoyalPurple800,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "${if (isHindi) role.labelHi else role.labelEn} • Tenant: $tenantSchoolId",
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = RoyalPurple900
            )
        }
    }
}
