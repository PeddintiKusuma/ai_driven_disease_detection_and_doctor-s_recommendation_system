export default function HealixLogo({ size = 'md', showText = true, className = '', textClassName = '' }) {
  const sizes = {
    sm: { img: 'w-8 h-8', text: 'text-lg' },
    md: { img: 'w-10 h-10', text: 'text-xl' },
    lg: { img: 'w-12 h-12', text: 'text-2xl' },
    xl: { img: 'w-16 h-16', text: 'text-3xl' },
  };
  const s = sizes[size] || sizes.md;

  return (
    <div className={`flex items-center gap-2.5 ${className}`}>
      <div className={`${s.img} rounded-xl bg-white border border-primary-100 shadow-sm flex items-center justify-center overflow-hidden shrink-0`}>
        <img
          src="/healix-logo.png"
          alt="Healix"
          className="w-[85%] h-[85%] object-contain"
        />
      </div>
      {showText && (
        <div>
          <span className={`${s.text} font-bold tracking-tight text-primary-900 block leading-none ${textClassName}`}>
            Healix
          </span>
          {size === 'lg' || size === 'xl' ? (
            <span className="text-[10px] uppercase tracking-widest text-primary-500 font-semibold mt-0.5 block">
              Healthcare
            </span>
          ) : null}
        </div>
      )}
    </div>
  );
}
