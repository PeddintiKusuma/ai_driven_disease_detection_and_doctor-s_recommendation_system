USE ai_healthcare;

-- Specializations
INSERT INTO specializations (name, description) VALUES
('General Physician', 'Primary care and general health consultations'),
('Cardiologist', 'Heart and cardiovascular system specialist'),
('Dermatologist', 'Skin, hair, and nail conditions specialist'),
('Pulmonologist', 'Respiratory system and lung diseases specialist'),
('Gastroenterologist', 'Digestive system and stomach diseases specialist'),
('Neurologist', 'Brain and nervous system specialist'),
('Nephrologist', 'Kidney diseases specialist'),
('Ophthalmologist', 'Eye diseases and vision specialist'),
('ENT Specialist', 'Ear, nose, and throat specialist'),
('Psychiatrist', 'Mental health conditions specialist'),
('Endocrinologist', 'Hormonal and metabolic disorders specialist'),
('Orthopedic', 'Bone and joint specialist'),
('Infectious Disease Specialist', 'Infectious diseases specialist');

-- Symptoms
INSERT INTO symptoms (name, description) VALUES
('fever', 'Elevated body temperature'),
('cough', 'Persistent coughing'),
('fatigue', 'General tiredness and weakness'),
('headache', 'Pain in the head region'),
('nausea', 'Feeling of sickness with inclination to vomit'),
('vomiting', 'Forceful expulsion of stomach contents'),
('diarrhea', 'Loose or watery stools'),
('chest_pain', 'Pain or discomfort in chest area'),
('shortness_of_breath', 'Difficulty breathing'),
('sore_throat', 'Pain or irritation in throat'),
('runny_nose', 'Nasal discharge'),
('joint_pain', 'Pain in joints'),
('muscle_pain', 'Pain in muscles'),
('abdominal_pain', 'Pain in stomach area'),
('dizziness', 'Feeling of unsteadiness'),
('skin_rash', 'Red irritated skin patches'),
('itching', 'Urge to scratch skin'),
('chills', 'Feeling of cold with shivering'),
('sweating', 'Excessive perspiration'),
('weakness', 'Lack of physical strength');

-- Diseases with specializations
INSERT INTO diseases (name, description, specialization_id, severity_level) VALUES
('Influenza', 'Viral respiratory infection', 1, 'MODERATE'),
('Common Cold', 'Mild viral upper respiratory infection', 1, 'LOW'),
('Pneumonia', 'Lung infection causing inflammation', 4, 'HIGH'),
('Asthma', 'Chronic respiratory condition', 4, 'MODERATE'),
('Bronchitis', 'Inflammation of bronchial tubes', 4, 'MODERATE'),
('Gastroenteritis', 'Stomach and intestinal inflammation', 5, 'MODERATE'),
('Migraine', 'Severe recurring headache disorder', 6, 'MODERATE'),
('Hypertension', 'High blood pressure condition', 2, 'MODERATE'),
('Heart Disease', 'Cardiovascular system disorder', 2, 'HIGH'),
('Diabetes', 'Blood sugar regulation disorder', 11, 'MODERATE'),
('Eczema', 'Skin inflammation condition', 3, 'LOW'),
('Allergic Reaction', 'Immune system overreaction', 3, 'MODERATE'),
('Urinary Tract Infection', 'Infection in urinary system', 7, 'MODERATE'),
('Anxiety Disorder', 'Mental health anxiety condition', 10, 'MODERATE'),
('Depression', 'Mental health mood disorder', 10, 'MODERATE'),
('Conjunctivitis', 'Eye inflammation (pink eye)', 8, 'LOW'),
('Sinusitis', 'Sinus inflammation', 9, 'LOW'),
('Arthritis', 'Joint inflammation', 12, 'MODERATE'),
('COVID-19', 'Coronavirus respiratory infection', 13, 'HIGH'),
('Food Poisoning', 'Illness from contaminated food', 5, 'MODERATE');

-- Healix platform doctors
INSERT INTO doctors (name, email, phone, specialization_id, qualification, experience, rating, consultation_fee, hospital, city, available_days, available_time, bio, license_number, approval_status) VALUES
('Dr. Priya Sharma', 'priya.sharma@healix.in', '+91-9848012345', 1, 'MBBS, MD (General Medicine)', 15, 4.9, 500.00, 'Apollo Hospital', 'Hyderabad', 'Mon,Tue,Wed,Thu,Fri', '09:00-17:00', 'Senior general physician with 15 years of experience in primary care, fever management, and preventive health checkups.', 'MCI-AP-2010-4521', 'APPROVED'),
('Dr. Arun Kumar', 'arun.kumar@healix.in', '+91-9848023456', 2, 'MBBS, MD (Cardiology), DM', 18, 4.8, 800.00, 'Heart Care Institute', 'Hyderabad', 'Mon,Wed,Fri', '10:00-18:00', 'Interventional cardiologist specializing in hypertension, chest pain evaluation, and cardiac risk assessment.', 'MCI-TG-2008-3312', 'APPROVED'),
('Dr. Sneha Reddy', 'sneha.reddy@healix.in', '+91-9848034567', 4, 'MBBS, MD (Pulmonology)', 12, 4.8, 750.00, 'Lung Health Center', 'Hyderabad', 'Mon,Tue,Thu,Fri', '08:00-15:00', 'Expert in respiratory conditions including asthma, bronchitis, cough, and influenza-related complications.', 'MCI-AP-2012-7789', 'APPROVED'),
('Dr. Amit Patel', 'amit.patel@healix.in', '+91-9848045678', 3, 'MBBS, MD (Dermatology)', 10, 4.7, 600.00, 'Skin Wellness Clinic', 'Bangalore', 'Tue,Thu,Sat', '09:00-16:00', 'Dermatologist treating eczema, allergic skin reactions, rashes, and general skin health concerns.', 'MCI-KA-2014-5567', 'APPROVED'),
('Dr. Ananya Gupta', 'ananya.gupta@healix.in', '+91-9848056789', 6, 'MBBS, MD (Neurology)', 13, 4.9, 900.00, 'Neuro Care Institute', 'Mumbai', 'Tue,Wed,Fri,Sat', '09:00-16:00', 'Neurologist focused on migraines, headaches, dizziness, and nerve-related symptom evaluation.', 'MCI-MH-2011-8890', 'APPROVED'),
('Dr. Vikram Singh', 'vikram.singh@healix.in', '+91-9848067890', 5, 'MBBS, MD (Gastroenterology)', 11, 4.6, 700.00, 'Digestive Care Hospital', 'Delhi', 'Mon,Wed,Thu,Sat', '10:00-17:00', 'Specialist in gastritis, food poisoning, abdominal pain, and digestive system disorders.', 'MCI-DL-2013-2234', 'APPROVED'),
('Dr. Lakshmi Iyer', 'lakshmi.iyer@healix.in', '+91-9848078901', 10, 'MBBS, MD (Psychiatry)', 16, 4.8, 800.00, 'Mind Wellness Center', 'Bangalore', 'Mon,Tue,Wed,Thu,Fri', '09:00-18:00', 'Psychiatrist providing care for anxiety, depression, stress-related conditions, and mental wellness.', 'MCI-KA-2009-6678', 'APPROVED'),
('Dr. Suresh Nair', 'suresh.nair@healix.in', '+91-9848089012', 11, 'MBBS, MD (Endocrinology)', 12, 4.7, 750.00, 'Diabetes Care Institute', 'Kochi', 'Mon,Wed,Fri,Sat', '08:00-16:00', 'Endocrinologist specializing in diabetes management, thyroid disorders, and metabolic health.', 'MCI-KL-2012-4456', 'APPROVED');

-- Admin: admin@healix.in / Admin@123 (created by backend seeder)
-- Doctors: *@healix.in / Doctor@123 (created by backend seeder on startup)

-- Patient reviews
INSERT INTO reviews (user_id, doctor_id, rating, comment) VALUES
(2, 1, 5, 'Very knowledgeable and caring doctor. Highly recommended!'),
(2, 2, 5, 'Excellent cardiologist, explained everything clearly.'),
(2, 4, 4, 'Good consultation for respiratory issues.');
