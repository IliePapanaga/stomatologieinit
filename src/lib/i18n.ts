import i18n from "i18next";
import { initReactI18next } from "react-i18next";

// English translation dictionary
const en = {
  translation: {
    // Topbar
    "switch_location": "Switch location",
    "impersonating": "Impersonating",
    "exit_impersonation": "Exit impersonation",
    "exit": "Exit",
    "search_placeholder": "Search professionals, postings, certificates…",
    "owner_settings": "Owner settings",
    "profile": "Profile",
    "billing": "Billing",
    "sign_out": "Sign out",
    
    // Sidebar Navigation
    "dashboard": "Dashboard",
    "postings": "Postings",
    "staff": "Staff",
    "schedule": "Schedule",
    "settings": "Settings",
    "assignments": "Assignments",
    "temporary_jobs": "Temporary Jobs",
    "permanent_jobs": "Permanent Jobs",
    "hidden_jobs": "Hidden Jobs",
    "job_history": "Job History",
    "documents": "Documents",
    "specialties": "Specialties",
    "overview": "Overview",
    "users": "Users",
    "reports": "Reports",
    "switch_context": "Switch context",
    "owner_view": "Owner view",
    "professional_view": "Professional view",
    
    // Dashboards
    "good_morning": "Good morning, {{name}}",
    "good_morning_fallback": "Good morning",
    "whats_happening_owner": "Here's what's happening across your owner dashboard today.",
    "whats_happening_pro": "Here's your professional overview for today.",
    "new_role": "New role",
    
    // Common metrics
    "active_postings": "Active Postings",
    "filled_today": "Filled Today",
    "pending_interviews": "Pending Interviews",
    "active_sos": "Active SOS Broadcasts",
    
    "this_week": "{{count}} this week",
    "match": "match",
    "today": "{{count}} today",
    "searching_now": "Searching now",
    
    "nearby_talent": "Nearby talent",
    "live": "Live",
    "professionals_within": "{{count}} professionals within {{dist}} mi",
    "online": "{{count}} online",
    
    "by_distance": "By distance",
    "by_specialty": "By specialty",
    "recent_activity": "Recent activity",
    "emergency_staffing": "Emergency Staffing",
    "system_console": "System Console",
    
    // Pro Dashboard
    "next_shift": "Next shift",
    "upcoming": "Upcoming",
    "hours": "hours",
    "earnings": "Earnings",
    "rating": "Rating",
    "reviews": "reviews",
    "open_shifts": "Open shifts near you",
    "view_all": "View all",
    
    "system_overview": "Global overview",
    "system_desc": "Platform-wide health, compliance, and operations across all tenants.",
    "audit_log": "Audit log",
    "service_health": "Service health",
    "latest_actions": "Latest privileged actions across the platform",
    "last_5_min": "Last 5 min · synthetic checks",
    
    // Status
    "all_systems_nominal": "All systems nominal",
    "uptime": "Uptime {{pct}}% · {{time}}d"
  }
};

// Spanish translation dictionary
const es = {
  translation: {
    // Topbar
    "switch_location": "Cambiar ubicación",
    "impersonating": "Personificando",
    "exit_impersonation": "Salir de la personificación",
    "exit": "Salir",
    "search_placeholder": "Buscar profesionales, ofertas, certificados…",
    "owner_settings": "Configuración del propietario",
    "profile": "Perfil",
    "billing": "Facturación",
    "sign_out": "Cerrar sesión",
    
    // Sidebar Navigation
    "dashboard": "Panel de control",
    "postings": "Ofertas de trabajo",
    "staff": "Personal",
    "schedule": "Horario",
    "settings": "Configuración",
    "assignments": "Asignaciones",
    "temporary_jobs": "Trabajos temporales",
    "permanent_jobs": "Trabajos permanentes",
    "hidden_jobs": "Trabajos ocultos",
    "job_history": "Historial de trabajo",
    "documents": "Documentos",
    "specialties": "Especialidades",
    "overview": "Resumen",
    "users": "Usuarios",
    "reports": "Informes",
    "switch_context": "Cambiar contexto",
    "owner_view": "Vista de propietario",
    "professional_view": "Vista profesional",
    
    // Dashboards
    "good_morning": "Buenos días, {{name}}",
    "good_morning_fallback": "Buenos días",
    "whats_happening_owner": "Esto es lo que sucede hoy en tu panel de propietario.",
    "whats_happening_pro": "Este es tu resumen profesional de hoy.",
    "new_role": "Nuevo rol",
    
    // Common metrics
    "active_postings": "Ofertas activas",
    "filled_today": "Cubiertos hoy",
    "pending_interviews": "Entrevistas pendientes",
    "active_sos": "Difusiones SOS activas",
    
    "this_week": "{{count}} esta semana",
    "match": "coincidencia",
    "today": "{{count}} hoy",
    "searching_now": "Buscando ahora",
    
    "nearby_talent": "Talento cercano",
    "live": "En vivo",
    "professionals_within": "{{count}} profesionales en {{dist}} mi",
    "online": "{{count}} en línea",
    
    "by_distance": "Por distancia",
    "by_specialty": "Por especialidad",
    "recent_activity": "Actividad reciente",
    "emergency_staffing": "Personal de emergencia",
    "system_console": "Consola del sistema",
    
    // Pro Dashboard
    "next_shift": "Siguiente turno",
    "upcoming": "Próximamente",
    "hours": "horas",
    "earnings": "Ganancias",
    "rating": "Calificación",
    "reviews": "reseñas",
    "open_shifts": "Turnos abiertos cerca de ti",
    "view_all": "Ver todo",
    
    "system_overview": "Resumen global",
    "system_desc": "Salud de la plataforma, cumplimiento y operaciones en todos los inquilinos.",
    "audit_log": "Registro de auditoría",
    "service_health": "Salud del servicio",
    "latest_actions": "Últimas acciones privilegiadas en la plataforma",
    "last_5_min": "Últimos 5 min · controles sintéticos",
    
    // Status
    "all_systems_nominal": "Todos los sistemas nominales",
    "uptime": "Tiempo en línea {{pct}}% · {{time}}d"
  }
};

i18n
  .use(initReactI18next)
  .init({
    resources: {
      en,
      es
    },
    lng: "en",
    fallbackLng: "en",
    interpolation: {
      escapeValue: false // react already safes from xss
    }
  });

export default i18n;
