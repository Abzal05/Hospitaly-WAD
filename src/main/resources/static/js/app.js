// Simple frontend JS to load doctors and patients via API and render into the page
async function fetchJson(url, opts){
  const res = await fetch(url, Object.assign({headers:{'Accept':'application/json','Content-Type':'application/json'}}, opts));
  if(!res.ok) throw new Error('Request failed: '+res.status);
  return res.json();
}

async function renderDoctors(containerId){
  const c = document.getElementById(containerId);
  if(!c) return;
  try{
    const page = await fetchJson('/api/doctors?page=0&size=20');
// ...existing code...
    if(page.content && page.content.length){
      page.content.forEach(p =>{
        const div = document.createElement('div');
        div.className='entity-card';
        div.innerHTML = `<div class="entity-avatar">${escapeHtml(initials(p.firstName,p.lastName))}</div><div><div style="font-weight:700">${escapeHtml(p.firstName||'')} ${escapeHtml(p.lastName||'')}</div><div style="color:var(--muted)">${escapeHtml(p.email||p.phone||'—')}</div></div>`;
        c.appendChild(div);
      })
    } else {
      c.innerHTML='<div class="empty">Пациенты не найдены</div>';
    }
  } catch(e){
    c.innerHTML = '<div class="empty">Ошибка при загрузке пациентов</div>';
    console.error(e);
  }
}

async function renderPatients(containerId){
  const c = document.getElementById(containerId);
  if(!c) return;
  try{
    const page = await fetchJson('/api/patients?page=0&size=20');
    c.innerHTML='';
    if(page.content && page.content.length){
      page.content.forEach(p =>{
        const div = document.createElement('div');
        div.className='entity-card';
        div.innerHTML = `<div class="entity-avatar">${escapeHtml(initials(p.firstName,p.lastName))}</div><div><div style="font-weight:700">${escapeHtml(p.firstName||'')} ${escapeHtml(p.lastName||'')}</div><div style="color:var(--muted)">${escapeHtml(p.email||p.phone||'—')}</div></div>`;
        c.appendChild(div);
      })
    } else {
      c.innerHTML='<div class="empty">No patients found</div>';
    }
  } catch(e){
    c.innerHTML = '<div class="empty">Failed to load patients</div>';
    console.error(e);
  }
}

// New: render recent appointments
async function renderAppointments(containerId){
  const c = document.getElementById(containerId);
  if(!c) return;
  try{
    const page = await fetchJson('/api/appointments?page=0&size=10');
    c.innerHTML='';
    if(page.content && page.content.length){
      const table = document.createElement('table');
// ...existing code...
      const thead = document.createElement('thead');
      thead.innerHTML='<tr><th>Время</th><th>Пациент</th><th>Врач</th><th>Статус</th></tr>';
      table.appendChild(thead);
      const tbody = document.createElement('tbody');
      page.content.forEach(a=>{
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${escapeHtml(a.appointmentDateTime||'—')}</td><td>${escapeHtml(a.patientId||'—')}</td><td>${escapeHtml(a.doctorId||'—')}</td><td>${escapeHtml(a.status||'—')}</td>`;
        tbody.appendChild(tr);
      });
      table.appendChild(tbody);
      c.appendChild(table);
    } else {
      c.innerHTML='<div class="empty">Приёмы не найдены</div>';
    }
  } catch(e){
    c.innerHTML = '<div class="empty">Ошибка при загрузке приёмов</div>';
    console.error(e);
  }
}

function initials(a,b){
  const fa = (a||'').trim(); const fb=(b||'').trim();
  return (fa.charAt(0)||'') + (fb.charAt(0)||'');
}

function escapeHtml(s){return String(s).replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":"&#39;"})[c]);}

// Toasts
function toast(msg, type='info'){
  const root = document.getElementById('toast-root');
  if(!root) return;
  const el = document.createElement('div'); el.className='toast'; el.innerHTML = `<strong>${escapeHtml(msg)}</strong>`;
  root.appendChild(el);
  setTimeout(()=>{ el.remove(); }, 4000);
}

// Modal helper
function showModal(html){
  const root = document.getElementById('modal-root');
  if(!root) return; root.style.display='block';
  root.innerHTML = `<div class="modal-backdrop" onclick="closeModal(event)"><div class="modal" onclick="event.stopPropagation()">${html}</div></div>`;
}
function closeModal(ev){ const root=document.getElementById('modal-root'); if(!root) return; root.style.display='none'; root.innerHTML=''; }

// Quick appointment modal
function openQuickAppointmentModal(){
  const html = `
    <h4>Quick appointment</h4>
    <form id="quick-appointment-form">
      <input class="input" name="patientId" placeholder="Patient ID" />
      <input class="input" name="doctorId" placeholder="Doctor ID" />
      <input class="input" name="hospitalId" placeholder="Hospital ID" />
      <input class="input" name="appointmentDateTime" placeholder="YYYY-MM-DDTHH:MM:SS" />
      <textarea class="input" name="reason" placeholder="Reason"></textarea>
      <div style="display:flex;gap:0.5rem;justify-content:flex-end;margin-top:0.5rem"><a class="btn" id="quick-submit">Create</a><a class="btn" onclick="closeModal()">Cancel</a></div>
    </form>
  `;
  showModal(html);
  document.getElementById('quick-submit').addEventListener('click', submitQuickAppointment);
}

async function submitQuickAppointment(){
  const form = document.getElementById('quick-appointment-form'); if(!form) return;
  const data = {
    patientId: parseInt(form.patientId.value),
    doctorId: parseInt(form.doctorId.value),
    hospitalId: parseInt(form.hospitalId.value),
    appointmentDateTime: form.appointmentDateTime.value,
    reason: form.reason.value
  };
  try{
    const res = await fetch('/api/appointments',{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(data)});
    if(!res.ok) throw new Error('Bad');
    toast('Appointment created','success');
    closeModal();
    // refresh appointments widget
    renderAppointments('appointments-widget');
  }catch(e){
    toast('Failed to create appointment','error');
    console.error(e);
  }
}

// CSV export
async function downloadReportCSV(){
  try{
    const res = await fetch('/api/appointments?page=0&size=100');
    if(!res.ok) throw new Error('Failed');
    const data = await res.json();
    const rows = [['id','patientId','doctorId','date','status','reason']];
    data.content.forEach(a=>rows.push([a.id,a.patientId,a.doctorId,a.appointmentDateTime,a.status,a.reason]));
    const csv = rows.map(r=>r.map(v=>`"${String(v||'').replace(/"/g,'""')}"`).join(',')).join('\n');
    const blob = new Blob([csv],{type:'text/csv'});
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a'); a.href=url; a.download='appointments.csv'; document.body.appendChild(a); a.click(); a.remove(); URL.revokeObjectURL(url);
    toast('CSV downloaded','success');
  }catch(e){toast('Failed to download','error');console.error(e)}
}

// On DOM ready load widgets if present
document.addEventListener('DOMContentLoaded',()=>{
  if(document.getElementById('doctors-widget')) renderDoctors('doctors-widget');
  if(document.getElementById('patients-widget')) renderPatients('patients-widget');
  if(document.getElementById('appointments-widget')) renderAppointments('appointments-widget');
});
