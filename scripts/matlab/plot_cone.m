r = linspace(0, 1.25) ;
th = linspace(0, 2*pi) ;
[R,T] = meshgrid(r,th) ;
X = R.*cos(T) ;
Y = R.*sin(T) ;
Z = R ;
contour(X,Y,Z)
colormap('winter')